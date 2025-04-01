package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.service.TicketOrderCalculator;
import uk.gov.dwp.uc.pairtest.validation.TicketOrderValidator;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;

/**
 * Implementation of the TicketService that processes ticket purchase requests.
 * This service validates the ticket request, calculates the payment and seat reservation,
 * and interacts with external payment and reservation services.
 * 
 * In a microservice architecture, this would be part of the order processing service
 * that connects to payment and reservation services via REST APIs.
 * 
 * @author Keyur Pandya
 */
public class TicketServiceImpl implements TicketService {

    private final TicketOrderValidator validator;
    private final TicketOrderCalculator calculator;
    private final TicketPaymentService paymentService;
    private final SeatReservationService seatService;

    /**
     * Default constructor for production environments.
     * In a cloud-based deployment, the dependency injection would be handled by
     * a framework like Spring Boot and configured for the specific environment.
     */
    public TicketServiceImpl() {
        this(new TicketPaymentServiceImpl(), new SeatReservationServiceImpl());
    }

    /**
     * Constructor with injectable services for better testability.
     * This pattern enables easier mocking in test environments.
     *
     * @param paymentService Payment service implementation
     * @param seatService Seat reservation service implementation
     */
    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService seatService) {
        this.validator = new TicketOrderValidator();
        this.calculator = new TicketOrderCalculator();
        this.paymentService = paymentService;
        this.seatService = seatService;
    }

    /**
     * Processes a ticket purchase request by validating the order,
     * calculating payment amount and seats needed, and executing 
     * the payment and seat reservation.
     *
     * @param accountId The account making the purchase
     * @param ticketTypeRequests The tickets being purchased
     * @throws InvalidPurchaseException if the purchase request is invalid
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validator.validate(accountId, ticketTypeRequests);

        int totalAmount = calculator.calculateTotalAmount(ticketTypeRequests);
        int seatsToReserve = calculator.calculateSeatsToReserve(ticketTypeRequests);

        paymentService.makePayment(accountId, totalAmount);
        seatService.reserveSeat(accountId, seatsToReserve);
    }
}
