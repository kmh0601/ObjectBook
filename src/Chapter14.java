import Chapter11_2.RatePolicy;
import Chapter11_2.Phone;
import Chapter4.Money;

import javax.print.attribute.standard.PresentationDirection;
import java.security.PublicKey;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Chapter14 {
    public static void main(String[] agrs){
        System.out.println("Chapter14");
    }
    public class FixedFeePolicy extends BasicRatePolicy{
        private Money amount;
        private Duration seconds;
        public FixedFeePolicy(Money amount, Duration seconds){
            this.amount = amount;
            this.seconds = seconds;
        }
        @Override
        protected Money calculateCallFee(Call call){
            return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
    public class DateTimeInterval{
        private LocalDateTime from;
        private LocalDateTime to;
        public List<DateTimeInterval> splitByDay(){
            if(days() > 0){
                return splitByDay(days());
            }
            return Arrays.asList(this);
        }
        private long days(){
            return Duration.between(from.toLocalDate().atStartOfDay(), to.toLocalDate().atStartOfDay()).toDays();
        }
        private List<DateTimeInterval> splitByDay(long days){
            List<DateTimeInterval> result = new ArrayList<>();
            addFirstDay(result);
            addMiddleDays(result, days);
            addLastDay(result);
            return result;
        }
        private void addFirstDay(List<DateTimeInterval> result){
            result.add(DateTimeInterval.toMidinight(from));
        }
        private void addMiddleDays(List<DateTimeInterval>result, long days){
            for(int loop = 1 ; loop<days ; loop++){
                result.add(DateTimeInterval.during(from.toLocalDate().plusDays(loop)));
            }
        }
        private void addLastDay(List<DateTimeInterval> result){
            result.add(DateTimeInterval.fromMidnight(to));
        }

        public static DateTimeInterval of(LocalDateTime from, LocalDateTime to){
            return new DateTimeInterval(from, to);
        }
        public static DateTimeInterval toMidinight(LocalDateTime from){
            return new DateTimeInterval(
                    from,
                    LocalDateTime.of(from.toLocalDate(), LocalTime.of(23,59,59,999_999_999)));
        }
        public static DateTimeInterval fromMidnight(LocalDateTime to){
            return new DateTimeInterval(LocalDateTime.of(to.toLocalDate(), LocalTime.of(0,0)),to);
        }
        public static DateTimeInterval during(LocalDate date){
            return new DateTimeInterval(
                    LocalDateTime.of(date, LocalTime.of(0,0)),
                    LocalDateTime.of(date, LocalTime.of(23,59,59,999_999_999)));
        }
        private DateTimeInterval(LocalDateTime from, LocalDateTime to){
            this.from = from;
            this.to = to;
        }
        public Duration duration(){
            return Duration.between(from,to);
        }
        public LocalDateTime getFrom(){
            return from;
        }
        public LocalDateTime getTo(){
            return to;
        }
    }
    public class Call{
        private DateTimeInterval interval;
        public List<DateTimeInterval> splitByDay(){
            return interval.splitByDay();
        }
        public Call(LocalDateTime from, LocalDateTime to){
            this.interval = DateTimeInterval.of(from,to);
        }
        public Duration getDuration(){
            return interval.duration();
        }
        public LocalDateTime getFrom(){
            return interval.getFrom();
        }
        public LocalDateTime getTo(){
            return interval.getTo();
        }
        public DateTimeInterval getInterval(){
            return interval;
        }
    }
    public class TimeOfDayDiscountPolicy extends BasicRatePolicy{
        private List<LocalTime> starts = new ArrayList<>();
        private List<LocalTime> ends = new ArrayList<>();
        private List<Duration> durations = new ArrayList<>();
        private List<Money> amounts = new ArrayList<>();

        @Override
        protected Money calculateCallFee(Call call){
            Money result = Money.ZERO;
            for(DateTimeInterval interval : call.splitByDay()){
                for(int loop = 0; loop < starts.size(); loop++){
                    result.plus(amounts.get(loop).times(
                            Duration.between(from(interval, starts.get(loop)), to(interval, ends.get(loop)))
                                    .getSeconds() / durations.get(loop).getSeconds()));
                }
            }
            return result;
        }
        private LocalTime from(DateTimeInterval interval, LocalTime from){
            return interval.getFrom().toLocalTime().isBefore(from) ?
                    from :
                    interval.getFrom().toLocalTime();
        }
        private LocalTime to(DateTimeInterval interval, LocalTime to){
            return interval.getTo().toLocalTime().isAfter(to) ?
                    to :
                    interval.getTo().toLocalTime();
        }
    }
    public class DayOfWeekDiscountRule{
        private List<DayOfWeek> dayOfWeeks = new ArrayList<>();
        private Duration duration = Duration.ZERO;
        private Money amount = Money.ZERO;

        public DayOfWeekDiscountRule(List<DayOfWeek> dayOfWeeks, Duration duration, Money amount){
            this.dayOfWeeks = dayOfWeeks;
            this.duration = duration;
            this.amount = amount;
        }
        public Money calculate(DateTimeInterval interval){
            if(dayOfWeeks.contains(interval.getFrom().getDayOfWeek())){
                return amount.times(interval.duration().getSeconds() / duration.getSeconds());
            }
            return Money.ZERO;
        }
    }
    public class DayOfWeekDiscountPolicy extends BasicRatePolicy{
        private List<DayOfWeekDiscountRule> rules = new ArrayList<>();
        public DayOfWeekDiscountPolicy(List<DayOfWeekDiscountRule> rules){
            this.rules = rules;
        }
        @Override
        protected Money calculateCallFee(Call call){
            Money result = Money.ZERO;
            for(DateTimeInterval interval : call.getInterval().splitByDay()){
                for(DayOfWeekDiscountRule rule : rules){
                    result.plus(rule.calculate(interval));
                }
            }
            return result;
        }
    }
    public class DurationDiscountRule extends FixedFeePolicy{
        private Duration from;
        private Duration to;
        public DurationDiscountRule(Duration from, Duration to, Money amount, Duration seconds){
            super(amount, seconds);
            this.from = from;
            this.to = to;
        }
        public Money calculate(Call call){
            if(call.getDuration().compareTo(to) > 0){
                return Money.ZERO;
            }
            if(call.getDuration().compareTo(to)<0){
                return Money.ZERO;
            }
            Phone phone = new Phone(null);
            phone.call(new Call(call.getFrom().plus(from),
                    call.getDuration().compareTo(to) > 0 ? call.getFrom().plus(to) : call.getTo()));
            return super.calculateFee(phone);
        }
    }
    public class DurationDiscountPolicy extends BasicRatePolicy{
        private List<DurationDiscountRule> rules = new ArrayList<>();
        public DurationDiscountPolicy(List<DurationDiscountRule> rules){
            this.rules = rules;
        }
        @Override
        protected Money calculateCallFee(Call call){
            Money result = Money.ZERO;
            for(DurationDiscountRule rule : rules){
                result.plus(rule.calculate(call));
            }
            return result;
        }
    }
    public interface FeeCondition{
        List<DateTimeInterval> findTimeIntervals(Call call);
    }
    public class FeeRule{
        private FeeCondition feeCondition;
        private FeePerDuration feePerDuration;

        public FeeRule(FeeCondition feeCondition, FeePerDuration feePerDuration){
            this.feeCondition = feeCondition;
            this.feePerDuration = feePerDuration;
        }
        public Money calculateFee(Call call){
            return feeCondition.findTimeIntervals(call)
                    .stream()
                    .map(each -> feePerDuration.calculate(each))
                    .reduce(Money.ZERO,(first, second) -> first.plus(second));
        }
    }
    public class FeePerDuration{
        private Money fee;
        private Duration duration;

        public FeePerDuration(Money fee, Duration duration){
            this.fee = fee;
            this.duration = duration;
        }
        public Money calculate(DateTimeInterval interval){
            return fee.times(Math.ceil((double)interval.duration().toNanos() / duration.toNanos()));
        }
    }
    public class BasicRatePolicy implements RatePolicy{
        private List<FeeRule> feeRules = new ArrayList<>();
        public BasicRatePolicy(FeeRule ... feeRules){
            this.feeRules = Arrays.asList(feeRules);
        }

        @Override
        public Money calculateFee(Phone phone){
            return phone.getCalls()
                    .stream()
                    .map(call -> calculte(call))
                    .reduce(Money.ZERO, (first, second) -> first.plus(second));
        }
        private Money calculate(Call call){
            return feeRules
                    .stream()
                    .map(rule -> rule.calculateFee())
                    .reduce(Money.ZERO, (first, second) -> first.plus(second));
        }
    }
    public class TimeOfDayFeeCondition implements FeeCondition{
        private LocalTime from;
        private LocalTime to;
        public TimeOfDayFeeCondition(LocalTime from, LocalTime to){
            this.from = from;
            this.to = to;
        }
        @Override
        public List<DateTimeInterval> findTimeIntervals(Call call){
            return call.getInterval().splitByDay()
                    .stream()
                    .filter(each -> from(each).isBefore(to(each)))
                    .map(each -> DateTimeInterval.of(
                            LocalDateTime.of(each.getFrom().toLocalDate(),from(each)),
                            LocalDateTime.of(each.getTo().toLocalDate(), to(each))))
                    .collect(Collectors.toList());
        }
    }
    public class DurationFeeCondition implements FeeCondition{
        private Duration from;
        private Duration to;
        public DurationFeeCondition(Duration from, Duration to){
            this.from = from;
            this.to = to;
        }
        @Override
        public List<DateTimeInterval> findTimeIntervals(Call call){
            if(call.getInterval().duration().compareTo(from)<0){
                return Collections.emptyList();
            }
            return Arrays.asList(DateTimeInterval.of(
                    call.getInterval().getFrom().plus(from),
                    call.getInterval().duration().compareTo(to) > 0 ?
                            call.getInterval().getFrom().plus(to) :
                            call.getInterval().getTo()));
        }
    }
    public class FixedFeelCondition implements FeeCondition{
        @Override
        public List<DateTimeInterval> findTimeIntervals(Call call){
            return Arrays.asList(call.getInterval());
        }
    }
}
