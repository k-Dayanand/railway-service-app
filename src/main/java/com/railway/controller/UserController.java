package com.railway.controller;

import com.railway.dto.BookingDTO;
import com.railway.model.*;
import com.railway.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService    userService;
    private final TrainService   trainService;
    private final BookingService bookingService;

    /** Helper: get current User entity */
    private User currentUser(UserDetails ud) {
        return userService.findByEmail(ud.getUsername());
    }

    // ── Dashboard ──────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = currentUser(ud);
        List<Ticket> tickets = bookingService.getUserTickets(user);
        long upcoming = tickets.stream()
            .filter(t -> t.getStatus() == Ticket.TicketStatus.BOOKED).count();
        double spent = tickets.stream()
            .filter(t -> t.getStatus() == Ticket.TicketStatus.BOOKED)
            .mapToDouble(Ticket::getAmount).sum();

        model.addAttribute("user",      user);
        model.addAttribute("tickets",   tickets.stream().limit(4).toList());
        model.addAttribute("totalTrips", tickets.size());
        model.addAttribute("upcoming",   upcoming);
        model.addAttribute("spent",      spent);
        return "user/dashboard";
    }

    // ── Search Trains ──────────────────────────────────────────
    @GetMapping("/search")
    public String searchPage(@RequestParam(required = false) String from,
                             @RequestParam(required = false) String to,
                             @AuthenticationPrincipal UserDetails ud,
                             Model model) {
        model.addAttribute("user", currentUser(ud));
        model.addAttribute("from", from);
        model.addAttribute("to",   to);
        if (from != null && !from.isBlank() && to != null && !to.isBlank()) {
            model.addAttribute("trains", trainService.searchTrains(from, to));
        }
        return "user/search";
    }

    // ── Book Ticket ────────────────────────────────────────────
 @GetMapping("/book/{trainId}")
public String bookPage(@PathVariable Long trainId,
                       @AuthenticationPrincipal UserDetails ud,
                       Model model) {

    User user = currentUser(ud);
    Train train = trainService.getTrainById(trainId);

    BookingDTO bookingDTO = new BookingDTO();
    bookingDTO.setTrainId(trainId);   // ⭐ VERY IMPORTANT LINE

    model.addAttribute("user", user);
    model.addAttribute("train", train);
    model.addAttribute("bookingDTO", bookingDTO);

    return "user/book";
}

    @PostMapping("/book")
    public String confirmBook(@Valid @ModelAttribute BookingDTO dto,
                              BindingResult result,
                              @RequestParam String paymentMethod,
                              @AuthenticationPrincipal UserDetails ud,
                              Model model,
                              RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("user",  currentUser(ud));
            model.addAttribute("train", trainService.getTrainById(dto.getTrainId()));
            return "user/book";
        }
        try {
            User   user   = currentUser(ud);
            Ticket ticket = bookingService.bookTicket(user, dto, paymentMethod);
            flash.addFlashAttribute("success",
                "🎉 Booking confirmed! Ticket #TK-" + String.format("%04d", ticket.getTicketId()));
            flash.addFlashAttribute("ticketId", ticket.getTicketId());
            return "redirect:/user/tickets";
        } catch (RuntimeException e) {
            model.addAttribute("user",  currentUser(ud));
            model.addAttribute("train", trainService.getTrainById(dto.getTrainId()));
            model.addAttribute("error", e.getMessage());
            return "user/book";
        }
    }

    // ── My Tickets ─────────────────────────────────────────────
    @GetMapping("/tickets")
    public String myTickets(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = currentUser(ud);
        model.addAttribute("user",    user);
        model.addAttribute("tickets", bookingService.getUserTickets(user));
        return "user/tickets";
    }

    // ── Cancel Ticket ──────────────────────────────────────────
    @PostMapping("/tickets/cancel/{ticketId}")
    public String cancelTicket(@PathVariable Long ticketId,
                               RedirectAttributes flash) {
        try {
            double refund = bookingService.cancelTicket(ticketId);
            flash.addFlashAttribute("success",
                String.format("Ticket cancelled. Refund of ₹%.2f will be credited in 3-5 days.", refund));
        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/tickets";
    }
        @GetMapping("/profile")
       public String profile(@AuthenticationPrincipal UserDetails ud,Model model) {

    User user = currentUser(ud);
    model.addAttribute("user", user);

    return "user/profile";
}
    
    }
