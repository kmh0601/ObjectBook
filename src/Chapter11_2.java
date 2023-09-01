import Chapter4.Money;
import Chapter11.Call;

import javax.swing.event.TableColumnModelListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chapter11_2 {
    public static void main(String[] args){
        System.out.println("Cahpter11_2");
    }

    public class Phone{
        private RatePolicy ratePolicy;
        private List<Call> calls = new ArrayList<>();
        public Phone(RatePolicy ratePolicy){
            this.ratePolicy = ratePolicy;
        }
        public List<Call> getCalls(){
            return Collections.unmodifiableList(calls);
        }
        public Money calculateFee(){
            return ratePolicy.calculateFee(this);
        }
    }
    public interface RatePolicy{
        Money calculateFee(Phone phone);
    }
    public abstract class BasicRatePolicy implements RatePolicy{
        @Override
        public Money calculateFee(Phone phone){
            Money result = Money.ZERO;
            for(Call call : phone.getCalls()){

            }
            return result;
        }
        protected abstract Money calculateCallFee(Call call);
    }
    public class RegularPolicy extends BasicRatePolicy{
        private Money amount;
        private Duration seconds;
        public RegularPolicy(Money amount, Duration seconds){
            this.amount = amount;
            this.seconds = seconds;
        }
        @Override
        protected Money calculateCallFee(Call call){
            return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
    public class NightlyDiscountPolicy extends BasicRatePolicy{
        private static final int LATE_NIGHT_HOUR = 22;
        private Money nightlyAmount;
        private Money regularAmount;
        private Duration seconds;
        public NightlyDiscountPolicy(Money nightlyAmount, Money regularAmount, Duration seconds){
            this.nightlyAmount = nightlyAmount;
            this.regularAmount = regularAmount;
            this.seconds = seconds;
        }
        @Override
        protected Money calculateCallFee(Call call){
            if(call.getFrom().getHour() >= LATE_NIGHT_HOUR){
                return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
            }
            return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
    public abstract class AdditionalRatePolicy implements RatePolicy{
        private RatePolicy next;
        public AdditionalRatePolicy(RatePolicy next){
            this.next = next;
        }
        @Override
        public Money calculateFee(Phone phone){
            Money fee = next.calculateFee(phone);
            return afterCalculated(fee);
        }
        abstract protected Money afterCalculated(Money fee);
    }
    public class TaxablePolicy extends AdditionalRatePolicy{
        private double taxRatio;
        public TaxablePolicy(double taxRatio, RatePolicy next){
            super(next);
            this.taxRatio = taxRatio;
        }
        @Override
        protected Money afterCalculated(Money fee){
            return fee.plus(fee.times(taxRatio));
        }
    }
    public class RateDiscountPolicy extends AdditionalRatePolicy{
        private Money discountAmount;
        public RateDiscountPolicy(Money discountAmount, RatePolicy next){
            super(next);
            this.discountAmount = discountAmount;
        }
        @Override
        protected Money afterCalculated(Money fee){
            return fee.minus(discountAmount);
        }
    }
}
