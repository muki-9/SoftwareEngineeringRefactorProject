package uk.ac.ucl.jsh;

public class SafeApplicationFactory {

	public Application mkSafeApplication(String application) {
        Application app;
        switch (application) {
            // case "cd":
            //     app = new Cd();
            //     break;
            // case "pwd":
            //     app = new Pwd();
            //     break;
            // case "ls":
            //     app = new Ls();
            //     break;
            // case "cat":
            // app = new Cat();
            //     break;
            case "echo":
                app = new Echo();
                break;
            // case "head":
            //     head(appArgs, writer);
            //     break;
            // case "tail":
            //     tail(appArgs, writer);
            //     break;
            // case "grep":
            //     grep(appArgs, writer);
            //     break;
            default:
                throw new RuntimeException(application + ": unknown application");
            }
		return app;
    }
}
