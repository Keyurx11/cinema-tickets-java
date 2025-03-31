package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketOrderValidator {

    private static final int MAX_TICKETS = 25;

    public void validate(Long accountId, TicketTypeRequest... requests) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Account ID must be a positive number");
        }

        int totalTickets = 0;
        int adultCount = 0;
        int childCount = 0;
        int infantCount = 0;

        for (TicketTypeRequest req : requests) {
            int count = req.getNoOfTickets();
            if (count < 0) {
                throw new InvalidPurchaseException("Ticket count cannot be negative");
            }

            totalTickets += count;

            switch (req.getTicketType()) {
                case ADULT -> adultCount += count;
                case CHILD -> childCount += count;
                case INFANT -> infantCount += count;
            }
        }

        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than 25 tickets");
        }

        if (adultCount == 0 && (childCount > 0 || infantCount > 0)) {
            throw new InvalidPurchaseException("Child or Infant tickets require at least one Adult");
        }

        if (totalTickets == 0) {
            throw new InvalidPurchaseException("At least one ticket must be purchased");
        }
    }
}
