package uk.ac.ucl.jsh;

import uk.ac.ucl.applications.*;

public class ApplicationFactory {

	public Application mkApplication(String application) {
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
            case "sed":
                app = new Sed();
                break;
            case "_cd":
                app = new UnsafeDecorator(new Cd());
                break;
            case "_pwd":
                app = new UnsafeDecorator(new Pwd());
                break;
            case "_ls":
                app = new UnsafeDecorator(new Ls());
                break;
            case "_cat":
                app = new UnsafeDecorator(new Cat());
                break;
            case "_echo":
                app = new UnsafeDecorator(new Echo());
                break;
            case "_head":
                app = new UnsafeDecorator(new Head());
                break;
            case "_tail":
                app = new UnsafeDecorator(new Tail());
                break;
            case "_grep":
                app = new UnsafeDecorator(new Grep());
                break;
			case "_wc":
                app = new UnsafeDecorator(new Wc());
                break;
            case "_sed":
                app = new UnsafeDecorator(new Sed());
                break;
            default:
                throw new RuntimeException(application + ": unknown application");
            }
		return app;
    }
}
