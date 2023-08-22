import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Chapter5 {
    public static void main(String[] args){
        System.out.println("Chapter5");
    }

    public class Screening{
        private Movie movie;
        private int sequence;
        private LocalDateTime whenScreened;
        public Reservation reserve(Customer customer, int audienceCount){
            return new Reservation(customer, this, calculateFee(audienceCount), audienceCount);
        }
        public Money calculateFee(int audienceCount){
            return movie.calculateMovieFee(this).times(audienceCount);
        }
        public int getSequence(){
            return sequence;
        }
        public LocalDateTime getWhenScreened(){
            return whenScreened;
        }
    }
    public abstract class Movie{
        private String title;
        private Money fee;
        private Duration runningTime;
        private List<DiscountCondition> discountConditions;

        public Movie(String title, Duration runningTime,Money fee,  DiscountCondition ... discountConditions){
            this.title = title;
            this.fee = fee;
            this.runningTime = runningTime;
            this.discountConditions = Arrays.asList(discountConditions);
        }
        public Money calculateMovieFee(Screening screening){
            if(isDiscountable(screening)){
                return fee.minus(calculateDiscountAmount());
            }

            return fee;
        }
        private boolean isDiscountable(Screening screening){
            return discountConditions.stream()
                    .anyMatch(condition -> condition.isSatisfiedBy(screening));
        }
        protected Money getFee(){
            return fee;
        }
        abstract protected Money calculateDiscountAmount();
    }
    public class AmountDiscountMovie extends Movie{
        private Money discountAmount;

        public AmountDiscountMovie(String title, Duration runningtime, Money fee, DiscountCondition ... discountConditions, Money discountAmount){
            super(title, runningtime, fee, discountConditions);
            this.discountAmount = discountAmount;
        }
        @Override
        protected Money calculateDiscountAmount(){
            return discountAmount;
        }
    }
    public class PercentDiscountMovie extends Movie{
        private double percent;

        public PercentDiscountMovie(String title, Duration runningtime, Money fee, DiscountCondition ... discountConditions, double percent){
            super(title, runningtime, fee, discountConditions);
            this.percent = percent;
        }
        @Override
        protected Money calculateDiscountAmount(){
            return getFee().times(percent);
        }
    }
    public class NoneDiscountMovie extends Movie{
        public NoneDiscountMovie(String title, Duration runningtime, Money fee){
            super(title, runningtime, fee);
        }
        @Override
        protected Money calculateDiscountAmount(){
            return Money.ZERO;
        }
    }

    public interface DiscountCondition{
        boolean isSatisfiedBy(Screening screening);
    }
    public class PeriodCondition implements DiscountCondition{
        private DayOfWeek dayOfWeek;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        public PeriodCondition(DayOfWeek dayOfWeek, LocalDateTime startTime, LocalDateTime endTime){
            this.dayOfWeek = dayOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        @Override
        public boolean isSatisfiedBy(Screening screening){
            return dayOfWeek.equals(screening.getWhenScreened().getDayOfWeek()) &&
                    startTime.compareTo(screening.getWhenScreened().toLocalTime()) <= 0 &&
                    endTime.compareTo(screening.getWhenScreened().toLocalTime()) >= 0;
        }
    }
    public class SequenceCondition implements DiscountCondition{
        private int sequence;
        public SequenceCondition(int sequence){
            this.sequence = sequence;
        }
        @Override
        private boolean isSatisfiedBy(Screening screening){
            return sequence == screening.getSequence();
        }
    }
    public enum MovieType{
        AMOUNT_DISCOUNT,
        PERCENT_DISCONT,
        NONE_DISCOUNT
    }
    public enum DiscountConditionType{
        SEQUENCE,
        PERIOD
    }
}
