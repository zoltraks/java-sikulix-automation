package pl.alyx.robot.sikulix;

public class Main {

    public static void main(String[] args)
    {
        try {
            new App()
                    .parse(args)
                    .initialize()
                    .start()
                    .dump();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

}
