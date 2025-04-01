package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.service.TicketOrderCalculator;
import uk.gov.dwp.uc.pairtest.validation.TicketOrderValidator;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;

public class TicketServiceImpl implements TicketService {

    private final TicketOrderValidator validator = new TicketOrderValidator();
    private final TicketOrderCalculator calculator = new TicketOrderCalculator();
    private final TicketPaymentService paymentService = new TicketPaymentServiceImpl();
    private final SeatReservationService seatService = new SeatReservationServiceImpl();

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validator.validate(accountId, ticketTypeRequests);

        int totalAmount = calculator.calculateTotalAmount(ticketTypeRequests);
        int seatsToReserve = calculator.calculateSeatsToReserve(ticketTypeRequests);

        paymentService.makePayment(accountId, totalAmount);
        seatService.reserveSeat(accountId, seatsToReserve);
    }
}
