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
        private boolean hasInvitation(){
            return invitation != null;
        }
        private void setTicket(Ticket ticket){
            this.ticket = ticket;
        }
        private void minusAmount(Long amount){
            this.amount -= amount;
        }
        public void plusAmount(Long amount){
            this.amount += amount;
        }

        public Long hold(Ticket ticket){
            if(hasInvitation()){
                setTicket(ticket);
                return 0L;
            }
            else{
                setTicket(ticket);
                minusAmount(ticket.getFee());
                return ticket.getFee();
            }
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
        public Long buy(Ticket ticket){
            return bag.hold(ticket);
        }
    }
    public class TicketOffice{
        private Long amount;
        private List<Ticket> tickets = new ArrayList<>();

        public TicketOffice(Long amount, Ticket...tickets){
            this.amount = amount;
            this.tickets.addAll(Arrays.asList(tickets));
        }

        private Ticket getTicket(){

            return tickets.remove(0);
        }
        public void minusAmount(Long amount){

            this.amount -= amount;
        }
        private void plusAmount(Long amount){

            this.amount += amount;
        }
        public void sellTicketTo(Audience audience){
            plusAmount(audience.buy(getTicket()));
            // TicketOffices 자율성을 높이기 위해 즉, 응집도를 높이기 위해 코드를 수정하니 기존에는 없던 TicketOffice와 Audience사이에 의존성이 생김
        }

    }
    public class TicketSeller{
        private TicketOffice ticketOffice;

        public TicketSeller(TicketOffice ticketOffice){
            this.ticketOffice = ticketOffice;
        }
        public void sellTo(Audience audience){
            ticketOffice.sellTicketTo(audience);
            // bag에 접근하는 것을 TicketSeller가 아니라 Audience가 수행함으로써 의존성을 낮춤(캡슐화)
            // 그 뒤에 TicketOffice의 필드에 TicketSeller가 접근하던 것을 TicketOffice가 내부에서 책임지게 함(캡슐화 - 응집도 up)
        }
    }
    public class Theater{
        private TicketSeller ticketSeller;
        public Theater(TicketSeller ticketSeller){
            this.ticketSeller = ticketSeller;
        }

        public void enter(Audience audience){
            ticketSeller.sellTo(audience);
            // TicketOffice에 접근하는 것을 TicketSeller가 수행함으로써 의존성을 낮춤(캡슐화)
        }
    }
}
