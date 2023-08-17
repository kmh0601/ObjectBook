import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chapter1 {
    public static void main(String[] args) {
        System.out.println("hello world");
    }

    public class Invitation{
        private LocalDateTime when;
    }

    public class Ticket{
        private Long fee;

        public long getFee(){
            return this.fee;
        }
    }

    public class Bag{
        private Long amount;
        private Invitation invitation;
        private Ticket ticket;

        public Bag(Long amount){
            this(null, amount);
        }
        public Bag(Invitation invitation, Long amount){
            this.invitation = invitation;
            this.amount = amount;
        }
        public boolean hasInvitation(){
            return invitation != null;
        }
        public void setTicket(Ticket ticket){
            this.ticket = ticket;
        }
        public void minusAmount(Long amount){
            this.amount -= amount;
        }
        public void plusAmount(Long amount){
            this.amount += amount;
        }
    }
    public class Audience{
        private Bag bag;

        public Audience(Bag bag){
            this.bag = bag;
        }
        public Bag getBag() {
            return bag;
        }
    }
    public class TicketOffice{
        private Long amount;
        private List<Ticket> tickets = new ArrayList<>();

        public TicketOffice(Long amount, Ticket...tickets){
            this.amount = amount;
            this.tickets.addAll(Arrays.asList(tickets));
        }

        public Ticket getTicket(){
            return tickets.remove(0);
        }
        public void minusAmount(Long amount){
            this.amount -= amount;
        }
        public void plusAmount(Long amount){
            this.amount += amount;
        }
    }
    public class TicketSeller{
        private TicketOffice ticketOffice;

        public TicketSeller(TicketOffice ticketOffice){
            this.ticketOffice = ticketOffice;
        }
        public TicketOffice getTicketOffice(){
            return this.ticketOffice;
        }
    }
    public class Theater{
        private TicketSeller ticketSeller;
        public Theater(TicketSeller ticketSeller){
            this.ticketSeller = ticketSeller;
        }

        public void enter(Audience audience){
            if(audience.getBag().hasInvitation()){
                Ticket ticket = ticketSeller.getTicketOffice().getTicket();
                audience.getBag().setTicket(ticket);
            }
            else{
                Ticket ticket = ticketSeller.getTicketOffice().getTicket();
                audience.getBag().setTicket(ticket);
                audience.getBag().minusAmount(ticket.getFee());
                ticketSeller.getTicketOffice().plusAmount(ticket.getFee());
            }
        }
    }
}
