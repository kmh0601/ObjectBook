import java.beans.Customizer;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Chapter4 {
    public static void main(String[] args){
        System.out.println("Chapter4");
    }
    public class ReservationAgency{
        public Reservation reserve(Screening screening, Customer customer, int audienceCount){
            Movie movie = screening.getMovie();

            boolean discountable = false;
            for(DiscountCondition condition : movie.getDiscountConditions()){
                if(condition.getType() == DiscountConditionType.PERIOD){
                    discountable = screening.getWhenScreened().getDayOfWeek().equals(condition.getDayOfWeek()) &&
                            condition.getStartTime().compareTo(screening.getWhenScreened().toLocalTime()) <= 0 &&
                            condition.getEndTime().compareTo(screening.getWhenScreened().toLocalTime()) >= 0;
                }else{
                    discountable = condition.getSequence() == screening.getSequence();
                }

                if(discountable){
                    break;
                }
            }

            Money fee;
            if (discountable){
                Money discountAmount = Money.ZERO;
                switch (movie.getMovieType()){
                    case AMOUNT_DISCOUNT:
                        discountAmount = movie.getDiscountAmount();
                        break;
                    case PERCENT_DISCOUNT:
                        discountAmount = movie.getFee().times(movie.getDiscountPercent());
                        break;
                    case NONE_DISCOUNT:
                        discountAmount = Money.ZERO;
                }
                fee = movie.getFee().minus(discountAmount);
            }else {
                fee = movie.getFee();
            }
            return new Reservation(customer, screening, fee, audienceCount);
        }
    }

    public class Money{
        public static final Money ZERO = Money.wons(0);

        private final BigDecimal amount;

        public static Money wons(long amount){
            return new Money(BigDecimal.valueOf(amount));
        }
        public static Money wons(double amount){
            return new Money(BigDecimal.valueOf(amount));
        }
        public Money(BigDecimal amount){
            this.amount = amount;
        }
        public Money plus(Money amount){
            return new Money(this.amount.add(amount.amount));
        }
        public Money minus(Money amount){
            return new Money(this.amount.subtract(amount.amount));
        }
        public Money times(double percent){
            return new Money(this.amount.multiply(BigDecimal.valueOf(percent)));
        }
        public boolean isLessThan(Money other){
            return amount.compareTo(other.amount) < 0;
        }
        public boolean isGreaterThanOrEqual(Money other){
            return amount.compareTo(other.amount) >= 0;
        }
    }
    public class Movie{
        private String title;
        private Duration runningTime;
        private Money fee;
        private List<DiscountCondition> discountConditions;
        private MovieType movieType;
        private Money discountAmount;
        private double discountPercent;

        public MovieType getMovieType() {
            return movieType;
        }
        public void setMovieType(MovieType movieType) {
            this.movieType = movieType;
        }

        public Money getFee() {
            return fee;
        }

        public void setFee(Money fee) {
            this.fee = fee;
        }

        public List<DiscountCondition> getDiscountConditions() {
            return discountConditions;
        }

        public void setDiscountConditions(List<DiscountCondition> discountConditions) {
            this.discountConditions = discountConditions;
        }

        public Money getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(Money discountAmount) {
            this.discountAmount = discountAmount;
        }

        public double getDiscountPercent(){
            return discountPercent;
        }
        public void setDiscountPercent(double discountPercent){
            this.discountPercent = discountPercent;
        }
    }
    public class DiscountCondition{
        private DiscountConditionType type;
        private int sequence;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;

        public DiscountConditionType getType() {
            return type;
        }

        public void setType(DiscountConditionType type) {
            this.type = type;
        }

        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }
    }
    public class Screening{
        private Movie movie;
        private int sequence;
        private LocalDateTime whenScreened;

        public Movie getMovie(){
            return movie;
        }
        public void setMovie(Movie moive){
            this.movie = moive;
        }

        public LocalDateTime getWhenScreened(){
            return whenScreened;
        }
        public void setWhenScreened(LocalDateTime whenScreened){
            this.whenScreened = whenScreened;
        }

        public int getSequence(){
            return sequence;
        }
        public void setSequence(int sequence){
            this.sequence = sequence;
        }
    }
    public class Reservation{
        private Customer customer;
        private Screening screening;
        private Money fee;
        private int audienceCount;

        public Reservation(Customer customer, Screening screening, Money fee, int audienceCount){
            this.customer = customer;
            this.screening = screening;
            this.fee = fee;
            this.audienceCount = audienceCount;
        }

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        public Screening getScreening() {
            return screening;
        }

        public void setScreening(Screening screening) {
            this.screening = screening;
        }

        public Money getFee() {
            return fee;
        }

        public void setFee(Money fee) {
            this.fee = fee;
        }

        public int getAudienceCount() {
            return audienceCount;
        }

        public void setAudienceCount(int audienceCount) {
            this.audienceCount = audienceCount;
        }
    }
    public class Customer{
        private String name;
        private String id;

        public Customer(String name, String id){
            this.name = name;
            this.id = id;
        }
    }

    public enum MovieType{
        AMOUNT_DISCOUNT,
        PERCENT_DISCOUNT,
        NONE_DISCOUNT
    }
    public enum DiscountConditionType{
        SEQUENCE,
        PERIOD
    }
}
