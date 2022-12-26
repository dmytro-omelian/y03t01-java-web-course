package com.agency.finalproject.controller;

import com.agency.finalproject.entity.Ticket;
import com.agency.finalproject.entity.TicketStatus;
import com.agency.finalproject.entity.User;
import com.agency.finalproject.service.feedback.FeedbackService;
import com.agency.finalproject.service.session.CurrentSession;
import com.agency.finalproject.service.session.Session;
import com.agency.finalproject.service.ticket.TicketService;
import com.agency.finalproject.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final UserService userService;
    private final TicketService ticketService;
    private final FeedbackService feedbackService;

    @Autowired
    public UserController(UserService userService, TicketService ticketService, FeedbackService feedbackService) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.feedbackService = feedbackService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void create(@RequestBody Ticket ticket) {
        this.ticketService.createTicket(ticket);
    }

    @RequestMapping(method = RequestMethod.POST, value = "feedback", params = {"ticketId", "text"})
    public void leaveFeedback(@RequestParam Long ticketId, @RequestParam String text) {
        Optional<Ticket> ticket = this.ticketService.getById(ticketId);
        if (!ticket.isPresent() || ticket.get().getStatus() != TicketStatus.DONE) {
            return;
        }
        this.feedbackService.submit(ticketId, text);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"email"})
    public List<Ticket> getTicketsByUserEmail(@RequestParam String email) {
        return this.ticketService.getTicketsByUserEmail(email);
    }

    // FIXME get by email or filter (Spring Security)?
    @RequestMapping(method = RequestMethod.GET, value = "/balance")
    public BigDecimal getCurrentBalance() {
        Session session = CurrentSession.getSession();
        try {
            Optional<User> user = this.userService.findByEmail(session.getEmail());
            if (!user.isPresent()) {
                logger.warn(String.format("User with email=[%s] was not found.", session.getEmail()));
                return BigDecimal.ZERO;
            }
            return user.get().getBalance();
        } catch (EntityNotFoundException e) {
            logger.error(String.format("Couldn't get current balance for user=[%s], see: %s", session.getEmail(), e));
            return BigDecimal.ZERO;
        }
    }

    @RequestMapping(method = RequestMethod.POST, params = {"ticketId", "userEmail"})
    public void payForTicket(@RequestParam Long ticketId, @RequestParam String userEmail) {
        try {
            this.userService.payForTicket(ticketId, userEmail);
        } catch (EntityNotFoundException e) {
            logger.warn(String.format("User with email=[%s] couldn't pay for ticket with id=[%d]", userEmail, ticketId));
        }
    }

}