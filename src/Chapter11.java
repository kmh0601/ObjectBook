import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import Chapter4.Money;
import jdk.jfr.Percentage;

public class Chapter11 {
    public static void main(String[] args){
        System.out.println("chapter11");
    }
    public class Call{
        private LocalDateTime from;
        private LocalDateTime to;
        public Call(LocalDateTime from, LocalDateTime to){
            this.from = from;
            this.to = to;
        }
        public Duration getDuration(){
            return Duration.between(from,to);
        }
        public LocalDateTime getFrom(){
            return from;
        }
    }
    public abstract class Phone{
        private List<Call> calls = new ArrayList<>();
        public Money calculateFee() {
            Money result = Money.ZERO;
            for(Call call : calls){
                result = result.plus(calculateCallFee());
            }
            return result;
        }
        protected Money afterCalculated(Money fee){
            return fee;
        }
        protected abstract Money calculateCallFee(Call call);
    }
    public class RegularPhone extends Phone{
        private Money amount;
        private Duration seconds;
        public RegularPhone(Money amount, Duration seconds){
            this.amount = amount;
            this.seconds = seconds;
        }
        @Override
        protected Money calculateCallFee(Call call){
            return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
    public class NightlyDiscountPhone extends Phone{
        private static final int LATE_NIGHT_HOUR = 22;

        private Money nightlyAmount;
        private Money regularAmount;
        private Duration seconds;
        public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds){
            this.nightlyAmount = nightlyAmount;
            this.regularAmount = regularAmount;
            this.seconds = seconds;
        }
        @Override
        protected Money calculateCallFee(Call call){
            if(call.getFrom().getHour() >= LATE_NIGHT_HOUR){
                return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
            }
            return regularAmount.times((call.getDuration().getSeconds() / seconds.getSeconds()));
        }

    }

    // 만약 여기서 세금 정책 2가지를 추가 한다고 하자. 그럼 요금 정책 2개와 세금 정책 2개를 조합 해야 하는데 이를 상속으로 구현해보자!
    public class TaxableRegularPhone extends RegularPhone{
        private double taxRate;
        public TaxableRegularPhone(Money amount, Duration seconds, double taxRate){
            super(amount, seconds);
            this.taxRate = taxRate;
        }
        @Override
        public Money calculateFee(){
            Money fee = super.calculateFee();
            return fee.plus(fee.times(taxRate));
        }
        @Override
        protected Money afterCalculated(Money fee){
            return fee.plus(fee.times(taxRate));
        }
    }
    public class TaxableNightlyDiscountPhone extends NightlyDiscountPhone{
        private double taxRate;
        public TaxableNightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds, double taxRate){
            super(nightlyAmount, regularAmount, seconds);
            this.taxRate = taxRate;
        }
        @Override
        protected Money afterCalculated(Money fee){
            return fee.plus(fee.times(taxRate));
        }
    }
    public class RateDiscountableRegularPhone extends RegularPhone{
        private Money discountAmount;
        public RateDiscountableRegularPhone(Money amount, Duration seconds, Money discountAmount){
            super(amount,seconds);
            this.discountAmount = discountAmount;
        }
        @Override
        protected Money afterCalculated(Money fee){
            return fee.minus(discountAmount);
        }
    }
    public class RateDiscountableNightlyDiscountPhone extends NightlyDiscountPhone{
        private Money discountAmount;
        public RateDiscountableNightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds, Money discountAmount){
            super(nightlyAmount, regularAmount, seconds);
            this.discountAmount = discountAmount;
        }
        @Override
        protected Money afterCalculated(Money fee){
            return fee.minus(discountAmount);
        }
    }

    // 이 뒤에 세금 정책을 두 개 동시에 지원하는 경우, 이때 순서도 고려하면 위 자식클래스들의 자식클래스들을 하나씩 더 만들어야 한다는 문제가 발생한다.
}
