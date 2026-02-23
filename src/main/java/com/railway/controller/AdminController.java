package com.railway.controller;

import com.railway.model.*;
import com.railway.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService    userService;
    private final TrainService   trainService;
    private final BookingService bookingService;
public AdminController(UserService userService,
                           TrainService trainService,
                           BookingService bookingService) {
        this.userService = userService;
        this.trainService = trainService;
        this.bookingService = bookingService;
    }

    // your methods below

    // ── Dashboard ──────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("admin",        userService.findByEmail(ud.getUsername()));
        model.addAttribute("totalUsers",   userService.countUsers());
        model.addAttribute("totalTrains",  trainService.countActiveTrains());
        model.addAttribute("totalBookings",bookingService.countBookings());
        model.addAttribute("revenue",      bookingService.totalRevenue());
        model.addAttribute("recentTickets",bookingService.getAllTickets().stream().limit(6).toList());
        model.addAttribute("trains",       trainService.getActiveTrains().stream().limit(5).toList());
        return "admin/dashboard";
    }

    // ── Train Management ───────────────────────────────────────
    @GetMapping("/trains")
    public String trainsPage(Model model) {
        model.addAttribute("trains", trainService.getAllTrains());
        model.addAttribute("train",  new Train());
        return "admin/trains";
    }

    @PostMapping("/trains/add")
    public String addTrain(@Valid @ModelAttribute Train train,
                           BindingResult result,
                           RedirectAttributes flash,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("trains", trainService.getAllTrains());
            model.addAttribute("error", "Please fix validation errors.");
            return "admin/trains";
        }
        try {
            trainService.saveTrain(train);
            flash.addFlashAttribute("success", "Train added successfully!");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/trains";
    }

    @GetMapping("/trains/edit/{id}")
    public String editTrainPage(@PathVariable Long id, Model model) {
        model.addAttribute("train",  trainService.getTrainById(id));
        model.addAttribute("trains", trainService.getAllTrains());
        model.addAttribute("editMode", true);
        return "admin/trains";
    }

    @PostMapping("/trains/edit/{id}")
    public String editTrain(@PathVariable Long id,
                            @Valid @ModelAttribute Train train,
                            BindingResult result,
                            RedirectAttributes flash,
                            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("trains", trainService.getAllTrains());
            model.addAttribute("editMode", true);
            return "admin/trains";
        }
        trainService.updateTrain(id, train);
        flash.addFlashAttribute("success", "Train updated successfully!");
        return "redirect:/admin/trains";
    }

    @PostMapping("/trains/delete/{id}")
    public String deleteTrain(@PathVariable Long id, RedirectAttributes flash) {
        trainService.deleteTrain(id);
        flash.addFlashAttribute("success", "Train deleted.");
        return "redirect:/admin/trains";
    }

    // ── All Bookings ───────────────────────────────────────────
    @GetMapping("/bookings")
    public String allBookings(Model model) {
        model.addAttribute("tickets", bookingService.getAllTickets());
        return "admin/bookings";
    }

    // ── All Users ──────────────────────────────────────────────
    @GetMapping("/users")
    public String allUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes flash) {
        userService.deleteUser(id);
        flash.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }
}
