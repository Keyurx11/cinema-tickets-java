package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    private TicketPaymentService paymentService;
    private SeatReservationService seatService;
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        paymentService = mock(TicketPaymentService.class);
        seatService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, seatService);
    }

    @Test
    void validSingleAdult_ShouldChargeAndReserveOneSeat() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        ticketService.purchaseTickets(1L, adult);

        verify(paymentService).makePayment(1L, 25);
        verify(seatService).reserveSeat(1L, 1);
    }

    @Test
    void validAdultAndChild_ShouldChargeAndReserveTwoSeats() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        ticketService.purchaseTickets(1L, adult, child);

        verify(paymentService).makePayment(1L, 40);
        verify(seatService).reserveSeat(1L, 2);
    }

    @Test
    void validAdultAndInfant_ShouldChargeAndReserveOneSeat() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(1L, adult, infant);

        verify(paymentService).makePayment(1L, 25);
        verify(seatService).reserveSeat(1L, 1);
    }

    @Test
    void multipleAdultsChildrenInfants_ShouldCalculateCorrectTotals() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        ticketService.purchaseTickets(2L, adult, child, infant);

        verify(paymentService).makePayment(2L, 95);
        verify(seatService).reserveSeat(2L, 5);
    }

    @Test
    void noAdult_ShouldThrowException() {
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, child));
    }

    @Test
    void onlyInfants_ShouldThrowException() {
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, infant));
    }

    @Test
    void moreThan25Tickets_ShouldThrowException() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, adult));
    }

    @Test
    void totalZeroTickets_ShouldThrowException() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 0);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, adult, child));
    }

    @Test
    void negativeTicketCount_ShouldThrowException() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, -1);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, adult, infant));
    }

    @Test
    void invalidAccountId_ShouldThrowException() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(0L, adult));
    }
}
