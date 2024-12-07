package encore.server.domain.user.enumerate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NickNameAnimal {
    LION("Lion"), TIGER("Tiger"), ELEPHANT("Elephant"),
    PANDA("Panda"), EAGLE("Eagle"), DOLPHIN("Dolphin"), BEAR("Bear"),
    WOLF("Wolf"), FOX("Fox"), GIRAFFE("Giraffe"), ZEBRA("Zebra"),
    RHINO("Rhino"), LEOPARD("Leopard"), KOALA("Koala"), GORILLA("Gorilla"),
    CHEETAH("Cheetah"), HIPPO("Hippo"), CROCODILE("Crocodile"), ALLIGATOR("Alligator"),
    KANGAROO("Kangaroo"), MONKEY("Monkey"), COW("Cow"), HORSE("Horse"),
    DONKEY("Donkey"), SHEEP("Sheep"), GOAT("Goat"), PENGUIN("Penguin"),
    OTTER("Otter"), SQUIRREL("Squirrel"), DEER("Deer"), LEMUR("Lemur"),
    MOOSE("Moose"), ARMADILLO("Armadillo"), WILDCAT("Wildcat"),
    CAMEL("Camel"), BAT("Bat"), BEAVER("Beaver"), SLOTH("Sloth"),
    LYNX("Lynx"), WALRUS("Walrus"), ANTELOPE("Antelope"), STINGRAY("Stingray");

    private final String animalName;

}
