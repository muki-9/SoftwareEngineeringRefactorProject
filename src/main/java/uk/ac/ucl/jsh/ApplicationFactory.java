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
            case "find":
                app = new Find();
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
            case "history":
                app = new History();
                break;
            case "exit":
                app = new Exit();
                break;
            case "mkdir":
                app = new Mkdir();
                break;
            case "rmdir":
                app = new Rmdir();
                break;
            case "_cd":
                app = new UnsafeDecorator(new Cd());
                break;
            case "_pwd":
                app = new UnsafeDecorator(new Pwd());
                break;
            case "_find":
                app = new UnsafeDecorator(new Find());
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
            case "_history":
                app = new UnsafeDecorator(new History());
                break;
            case "_exit":
                app = new UnsafeDecorator(new Exit());
                break;
            case "_mkdir":
                app = new UnsafeDecorator(new Mkdir());
                break;
            case "_rmdir":
                app = new UnsafeDecorator(new Rmdir());
                break;
            default:
                throw new RuntimeException(application + ": unknown application");
            }
		return app;
    }
}
