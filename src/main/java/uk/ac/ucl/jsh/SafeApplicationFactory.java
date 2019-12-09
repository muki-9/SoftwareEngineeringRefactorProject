package uk.ac.ucl.jsh;

import uk.ac.ucl.applications.*;

public class SafeApplicationFactory {

	public Application mkSafeApplication(String application) {
        Application app;
        switch (application) {
            case "cd":
                app = new Cd();
                break;
            case "pwd":
                app = new Pwd();
                break;
            case "ls":
                app = new Ls();
                break;
            case "cat":
                app = new Cat();
                break;
            case "echo":
                app = new Echo();
                break;
            case "head":
                app = new Head();
                break;
            case "tail":
                app = new Tail();
                break;
            case "grep":
                app = new Grep();
                break;
			case "wc":
                app = new Wc();
                break;
            default:
                throw new RuntimeException(application + ": unknown application");
            }
		return app;
    }
}
