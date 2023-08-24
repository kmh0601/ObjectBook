## Object 를 공부하며 느낀 점 정리

# Chapter 1

Theme1
- 비지니스 로직을 객체들의 상호작용임을 고려하면서 class를 작성하는게 인상깊음
  => 특히 class의 상호작용을 위한 class를 만드는게 인상깊음
- 생성자를 만들때 매개변수를 모두 고려한 생성자를 밑에 두고 위에 매개변수의 일부만을 사용하는 생성자를 만들면서
  This(null, 입력받은 매개변수) 형식으로 호출하는 것이 인상깊음
- 가변인자 … 는 내부적으로 배열 객체를 형성함 => 향상된 for문 사용가능

Theme2
- module(크기와 상관없이 클래스, 패키지, 라이브러리와 같은 프로그램을 구성하는 임의의 요소)
  의 3가지 기능 -> 1. 실행 중에 제대로 동작 2. 변경을 위해 존재(변경의 용이성) 3. 코드를 읽는 사람과의 의사소통
- 초기의 코드는 각 클래스들이 서로의 내부에 너무 깊게 관여해있다(결합도가 높다). 즉 객체 사이의 의존성이 크다. 따라서 특정 객체에 대한 변경이 부담스럽다. => 기능을 단순히 나누어서 각 객체들에 나누어 담는 것 만으로는 객체지향 설계가 아님 -> 불필요한 의존성을 제거할 필요가 있다, 객체 사이의 결합도를 낮추고 변경하기 쉬운 코드를 작성할 필요가 있다(인터페이스 - 구현)
- 불필요한 의존성을 제거하는 방법
    1. 객체의 자율성을 높이자
       -> 어떤 객체에 대한 접근제한을 특정 객체로 제한함으로써 어떤 객체에 대한 접근은 오직 특정 객체에 의해서만 		    가능하게 되고 이는 특정 객체가 스스로 어떤 일을 수행해야 함을 의미
       -> 캡슐화 : 개념적이나 물리적으로 객체 내부의 세부적인 사항을 감추는 것(객체 내부로의 접근을 제한)
       =자기 문제를 스스로 해결하도록 코드를 수정 = 응집도를 높임(연관성 없는 작업을 다른 객체에 위임)

수정 전 => 절차적 프로그래밍, 프로세스(기능)는 특정 객체에서만 담당하고 나머지 객체들은 데이터의 역할을 수행
= 데이터와 프로세스가 동일한 모듈 내부에 있지 않음
수정 후 => 객체지향 프로그래밍, 각 객체가 자신의 데이터에 대해서 스스로 처리함, 객체 간에는 소통
= 데이터와 프로세스가 동일한 모듈 내부에 위치함

### 객체지향의 세계에서는 모든 것이 능동적이고 자율적인 존재이다 -> 의인화

# Chapter2 / 객체지향 구현

Theme1

- 객체지향은 클래스가 아닌 객체에 초점을 맞추고 설계해야 한다
  -> 클래스란 결국 공통적인 상태와 행동을 공유하는 객체들을 추상화한 것에 불과하다

- 도메인
  -> 문제를 해결하기 위해 사용자가 프로그램을 사용하는 분야 -> 웹개발에서는 비지니스 로직에 해당한다고 보면 됨
  -> 도메인 모델을 형성해봄으로써 어떤 객체가 필요하고 서로 상호작용하는지 구상한 후에 구현을 한다

- 클래스 접근제어
  -> 객체의 속성에는 직접 접근할 수 없도록 막고 적절한 public메서드를 통해서만 내부 상태를 변경할 수 있게 해야함
  -> 경계의 명확성이 자율성을 보장

- 추상클래스, 인터페이스를 처음 써 봄
  -> 영화의 두 가지 할인정책을 만약 나에게 구현하라고 했으면 int discountPolicy = 0 or 1 로 해서 매개변수로 간단하게 구분하고 메서드 내에서 if문으로 0,1일때 각각을 계산하게 했을 것 같다. 하지만 discountPolicy를 상속하는 두 클래스를 만들어 내부에서 계산도 처리하도록 클래스를 만드니까 코드가 훨씬 보기 편하고 아름답다고 느낀다. 매우 인상깊다

Theme2

- 코드의 의존성과 실행 시점의 의존성이 다를 수 있다 -> 코드 수준에서는 인터페이스에 의존, 실행 시점에서는 인터페이스를 구현한 특정 인스턴스에 의존 -> 유연성, 확장가능성 = 다형성 하지만 디버깅 등이 어렵다 (트레이드오프)
- 다형성의 핵심은 동적 바인딩에 있다. 즉, 메시지와 메서드를 런타임 시점에서 바인딩 하는 것이다. 이는 컴파일 시점의 의존성과 런타인 시점의 의존성을 분리하는 지연(동적) 바인딩 메커니즘에 의해 이루어진다.
- 항상 예외 케이스를 최소화하고 일관성을 유지할 수 있는 방법을 선택 => 예를 들어 할인 정책이 없는 영화의 경우 Movie 객체 내부에서 if문을 통해 할인 정책이 없을 경우 영화Fee를 그대로 반환하도록 한다고 하자. 이렇게 되면 영화의 할인을 계산하는 역할을 DiscountPolicy클래스에 위임했던 일관성이 무너진다. 따라서 이런 if else문의 남용을 주의하자! 매우 인상깊음

Theme3

- 상속은 코드 재사용을 위해 널리 사용되는 기법이지만 두 가지 관점에서 설계에 좋지 않은 영향을 끼침
    1. 캡슐화 위반 -> 부모 클래스의 내부 구조를 잘 알아야 함
       2.설계가 유연하지 않음 -> 실행 시점에 객체의 종류를 변경하는 것이 불가능하다
- 이 단점을 해결하는 코드 재사용 방법은 합성 -> 객체 내부에 객체의 인스턴스를 사용해서 인터페이스에 정의된 메시지를 통해서만 코드를 재사용하는 방법이다

Chapter3 / 객체지향 패러다임의 본질 (역할, 책임, 협력)

Theme1
- 협력은 시스템이 제공해야할 기능적 문맥을 의미한다
- 역할이란 대체 가능한 책임들이 모여 형성된다 => 큰 범위의 역할에 대해 슬롯처럼 책임들을 교체할 수 있다 = 다형성
- 책임이란 협력에 참여하기 위해 객체가 수행하는 행동을 의미한다 -> 하는 것, 아는 것
  => 책임은 해당 책임을 수행하는데 있어 필요한 정보를 많이 가지고 있는 ‘정보전문가’에게 할당하는것이 바람직하다
- 객체가 메시지를 결정하는 것이 아니라 메시지가 객체를 결정한다
- 객체의 행동이 상태를 결정한다. 객체의 상태는 결국 행동을 위한 재료에 불과하다
- 객체는 다양한 역할을 가질 수 있다. 이때 하나의 협력에서 하나의 객체는 하나의 역할로 보여진다. 협력의 관점에서는 동일한 역할을 수행하는 객체들은 서로 대체가능하다(다형성). 역할은 특정한 객체의 종류를 캡슐화 한다.(해당 역할을 수행할 수 있다는 정보만 가질 뿐 해당 객체의 세부 구현을 알지는 못한다.)

Chapter4 / 설계품질과 트레이드오프(책임 중심 설계와 데이터 중심 설계를 비교하며)

Theme1
- 객체지향 설계란 올바른 객체에게 올바른 책임을 할당하면서 낮은 결합도와 높은 응집도를 가진 구조를 창조하는 활동이다

Theme2
- 캡슐화, 응집도, 결합도 세 가지 기준으로 책임 중심 설계와 데이터 중심 설계를 비교
  -> 캡슐화 : 객체지향 설계가 널리 쓰이는 이유는 변경에 대한 파급효과를 조절할 수 있기 때문이다
  변경 가능성이 높은 부분은 내부에 숨기고 외부에는 상대적으로 안정적인 부분만 공개한다.
  변경 가능성이 높은 부분을 ‘구현’, 상대적으로 안정적인 부분을 ‘인터페이스’라고 한다.
  -> 응집도 : 높을 수록 한 모듈의 변경에 대한 파급효과가 적다
  -> 결합도 : 높을 수록 한 모듈의 변경에 대한 파급효과가 크다
- 데이터 중심 설계의 문제점 : 캡슐화 위반(객체의 내부 구현을 인터페이스의 일부로 만듬), 높은 결합도, 낮은 응집도
  ex) Movie클래스에서는 퍼블릭 메서드를 통해서만 내부 상태에 접근 가능 -> 이건 캡슐화가 아니다! 퍼블릭 인터페이스에 Movie 내부에 Money 타입의 fee 인스턴스 변수가 존재함을 드러내기 때문이다.
- 결국 데이터 중심 설계는 전체 시스템을 하나의 거대한 의존성 덩어리로 만들어 버리기 때문에 변경에 매우 취약해진다


Chapter5 / 책임 주도 설계를 향해

Theme1
- 데이터 중심 설계에서 책임 중심의 설계로의 전환 : 1.데이터보다 행동을 먼저 결정 2. 협력이라는 문맥 안에서 책임을 결정
- 시스템 책임 파악 -> 책임 분할 -> 책임 할당 -> 다른 객체의 도움이 필요한 경우 적절한 역할 설정 -> 책임 할당(협력)
- 최초 메시지(클라이언트의 요구) -> 메시지 수신 객체 설정 -> 책임에 필요한 데이터는 무엇인가? -> 자신이 수행 할 수 없는 책임 -> 메시지 송신 -> 반복
- 메시지는 송신자의 의도를 반영해야 한다 = 수신자의 구현에 대해 고려하지 않음(캡슐화)
- 서로 다른 이유로 변경되는 두 개의 메서드를 가진다 = 응집도가 낮다
  -> 응집도가 낮은 클래스의 특징 : 클래스의 속성이 서로 다른 시점에 초기화 되거나 일부만 초기화 됨
  -> 메서드들이 사용하는 속성에 따라 그룹이 나뉨
- GRASP패턴
  -> 낮은 결합도, 높은 응집도
  -> 정보전문가
  -> 도메인 모델
  -> 창조자 패턴 : 객체A를 생성하는 책임을 객체B에게 맡긴다. -> B: A객체를 포함 또는 참조, 기록, 사용, A객체를
  초기화 하는데 필요한 데이터 지님

Theme2
- 하지만 처음부터 적절한 책임을 적절한 객체에게 부여하는 것은 쉽지 않다 -> 선구현(실행에 목적) 후에 리팩터링을 통해 내부구조를 변경한다.
- chapter4에서 데이터 중심으로 설계한 코드를 리팩터링 하는 과정을 통해 리팩터링이 어떻게 진행되는지 알아본다

 		