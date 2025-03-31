package uk.gov.dwp.uc.pairtest.service;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class TicketOrderCalculator {

    public int calculateTotalAmount(TicketTypeRequest... requests) {
        int total = 0;

        for (TicketTypeRequest req : requests) {
            int count = req.getNoOfTickets();

            switch (req.getTicketType()) {
                case ADULT -> total += count * 25;
                case CHILD -> total += count * 15;
                case INFANT -> total += 0;
            }
        }

        return total;
    }

    public int calculateSeatsToReserve(TicketTypeRequest... requests) {
        int seats = 0;

        for (TicketTypeRequest req : requests) {
            switch (req.getTicketType()) {
                case ADULT, CHILD -> seats += req.getNoOfTickets();
                case INFANT -> {} // no seat
            }
        }

        return seats;
    }
}
