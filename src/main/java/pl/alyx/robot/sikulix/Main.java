package pl.alyx.robot.sikulix;

public class Main {

    public static void main(String[] args)
    {
        //new Test().run();
        try {
            new App()
                    .parseArgs(args)
                    .initialize()
                    .start()
                    .dump();
        } catch (Exception x) {
            if (x.getMessage() != null && x.getMessage().length() > 0) {
                x.printStackTrace();
            }
        }
    }

}
