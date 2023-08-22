import java.awt.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Chapter2 {
    public static void main(String[] args){
        System.out.println("hello");
    }
    public class Screening{
        private Movie movie;
        private int sequence;
        private LocalDateTime whenScreened;

        public Screening(Movie movie, int sequence, LocalDateTime whenScreened){
            this.movie = movie;
            this.sequence = sequence;
            this.whenScreened = whenScreened;
        }

        public  LocalDateTime getWhenScreened(){
            return whenScreened;
        }
        public boolean isSequence(int sequence){
            return this.sequence == sequence;
        }
        public Money getMoviFee(){
            return movie.getFee();
        }
        public Reservation reserve(Customer customer, int audienceCount){
            return new Reservation(customer, this, calculateFee(audienceCount), audienceCount);
        }
        private Moeny calculateFee(int audienceCount){
            return movie.calculateMovieFee(this).times(audienceCount);
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
    }
    public class Movie{
        private String title;
        private Duration runningtime;
        private Money fee;
        private DiscountPolicy discountPolicy;

        public Movie(String title, Duration runningtime, Money fee, DiscountPolicy discountPolicy){
            this.title = title;
            this.runningtime = runningtime;
            this.fee = fee;
            this.discountPolicy = discountPolicy;
        }
        public Money getFee(){
            return this.fee;
        }
        public Money calculateMovieFee(Screening screening){
            return this.fee.minus(discountPolicy.calculateDiscountAmount(screening));
        }
    }
    public interface DiscountPolicy{
        Money calculateDiscountAmount(Screening screening);
    }
    public abstract class DefaultDiscountPolicy implements DiscountPolicy{
        private List<DiscountCondition> conditions = new ArrayList<>();
        public DefaultDiscountPolicy(DiscountCondition ... discountConditions){
            this.conditions = Arrays.asList(discountConditions);
        }
        @Override
        public Money calculateDiscountAmount(Screening screening){
            for(DiscountCondition each : conditions){
                if(each.isSatisfiedBy(screening)){
                    return getDiscountAmount(screening);
                }
            }
            return Money.ZERO;
        }
        abstract protected Money getDiscountAmount(Screening screening);
    }
    public interface DiscountCondition{
        boolean isSatisfiedBy(Screening screening);
    }
    public class SequenceCondition implements DiscountCondition{
        private int sequence;

        public SequenceCondition(int sequence){
            this.sequence = sequence;
        }
        @Override
        public boolean isSatisfiedBy(Screening screening){
            return screening.isSequence(sequence);
        }
    }
    public class PeriodCondition implements DiscountCondition{
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;

        public PeriodCondition(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
            this.dayOfWeek = dayOfWeek;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public boolean isSatisfiedBy(Screening screening) {
            return screening.getStartTime().getDayOfWeek().equals(dayOfWeek) &&
                    startTime.compareTo(screening.getStartTime().toLocalTime()) <= 0 &&
                    endTime.compareTo(screening.getStartTime().toLocalTime()) >= 0;
        }
    }
    public class AmountDiscountPolicy extends DefaultDiscountPolicy{
        private Money discountAmount;
        public AmountDiscountPolicy(Money discountAmount, DiscountCondition ... conditions){
            super(conditions);
            this.discountAmount = discountAmount;
        }
        @Override
        protected Money getDiscountAmount(Screening screening){
            return discountAmount;
        }
    }
    public class PercentDiscountPolicy extends DefaultDiscountPolicy{
        private double percent;
        public PercentDiscountPolicy(double percent, DiscountCondition ... discountConditions){
            super(discountConditions);
            this.percent = percent;
        }

        @Override
        protected Money getDiscountAmount(Screening screening) {
            return screening.getMoviFee().times(percent);
        }
    }
    public class NoneDiscountPolicy implements DiscountPolicy{
        @Override
        public Money calculateDiscountAmount(Screening screening){
            return Money.ZERO;
        }
    }

}
