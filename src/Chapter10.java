import javax.crypto.Cipher;
import javax.print.attribute.standard.PresentationDirection;
import java.security.PublicKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Chapter10 {
    public static void main(String[] args){
        System.out.println("chapter10");
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
    public class RegularPhone extends Phone{
        private Money amount;
        private Duration seconds;

        public RegularPhone(Money amount, Duration seconds, double taxRate){
            super(taxRate);
            this.amount = amount;
            this.seconds = seconds;
        }
        @Override
        private Money calculateCallFee(Call call){
            return amount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
    public class NightlyDiscountPhone extends Phone{
        private static final int LATE_NIGHT_HOUR = 22;
        private Money nightlyAmount;
        private Money regularAmount;
        private Duration seconds;
        public NightlyDiscountPhone(Money nightlyAmount, Money regularAmount, Duration seconds, double taxRate){
            super(taxRate);
            this.nightlyAmount = nightlyAmount;
            this.regularAmount = regularAmount;
            this.seconds = seconds;
        }

        @Override
        private Money calculateCallFee(Call call){
            if(call.getFrom().getHour() >= LATE_NIGHT_HOUR){
                return nightlyAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
            }
            return regularAmount.times(call.getDuration().getSeconds() / seconds.getSeconds());
        }
    }
    public abstract class Phone{
        private List<Call> calls = new ArrayList<>();
        private double taxRate;
        public Phone(double taxRate){
            this.taxRate = taxRate;
        }

        public Money calculateFee(){
            Money result = Money.ZERO;

            for(Call call : calls){
                result = result.plus(calculateCallFee(call));
            }
            return result.plus(result.tiems(taxRate));
        }
        abstract protected Money calculateCallFee(Call call);
    }




    // 상속으로 인해 부모 클래스의 변경이 자식 클래스에게 까지 전파되는 것을 알아보기 위한 클래스
    public class Song{
        private String title;
        private String singer;

        public Song(String singer, String title){
            this.singer = singer;
            this.title = title;
        }
        public String getTitle(){
            return title;
        }
        public String getSinger(){
            return singer;
        }
    }
    public class Playlist{
        private List<Song> tracks = new ArrayList<>();
        private Map<String,String> singers = new HashMap<>();

        public void append(Song song){
            getTracks().add(song);
            singers.put(song.getSinger(), song.getTitle());
        }
        public List<Song> getTracks(){
            return this.tracks;
        }
        public Map<String,String> getSingers(){
            return singers;
        }
    }
    public class PersonalPlaylist extends Playlist{
        public void remove(Song song){
            getTracks().remove(song);
            getSingers().remove(song.getSinger());
        }
    }
}

