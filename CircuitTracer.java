import java.awt.Point;
import java.util.ArrayList;
import java.io.FileNotFoundException;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail
 */
public class CircuitTracer {

	/**
	 * Launch the program.
	 * 
	 * @param args three required arguments:
	 *             first arg: -s for stack or -q for queue
	 *             second arg: -c for console output or -g for GUI output
	 *             third arg: input file name
	 */
	public static void main(String[] args) {
		new CircuitTracer(args); // create this with args
	}

	/** Print instructions for running CircuitTracer from the command line. */
	private void printUsage() {
		// Command line arguments
		System.out.println("Usage: java CircuitTracer -s|-q -c|-g <filename>");
		System.out.println("  -s for stack or -q for queue");
		System.out.println("  -c for console output or -g for GUI output");

	}

	/**
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * 
	 * @param args command line arguments passed through from main()
	 */
	public CircuitTracer(String[] args) {
		// parse and validate command line args - first validation provided
		ArrayList<TraceState> bestPaths = new ArrayList<TraceState>();
		CircuitBoard board = null;
		if (args.length != 3) {
			printUsage();
			return;
		}
		if (!args[0].equals("-s") && !args[0].equals("-q")) {
			printUsage();
			return;
		}
		if (!args[1].equals("-c") && !args[1].equals("-g")) {
			printUsage();
			return;
		}
		// initialize the Storage to use either a stack or queue
		Storage<TraceState> storage = null;
		switch (args[0]) {
			case "-s":
				storage = Storage.getStackInstance();
				break;
			case "-q":
				storage = Storage.getQueueInstance();
				break;
			default:
				printUsage();
				break;
		}
		// read in the CircuitBoard from the given file
		try {
			board = new CircuitBoard(args[2]);
		} catch (FileNotFoundException e) {
			System.out.println(e + "File not found");
		} catch (InvalidFileFormatException e) {
			System.out.println(e + "Invalid file format");
		}
		// run the search for best paths
		int x = board.getStartingPoint().x;
		int y = board.getStartingPoint().y;
		// TraceState startingState = new TraceState(board, x, y);
		// storage.store(startingState);

		// Check right
		if (board.isOpen(x + 1, y)) {
			storage.store(new TraceState(board, x + 1, y));
		}

		// Check left
		if (board.isOpen(x - 1, y)) {
			storage.store(new TraceState(board, x - 1, y));
		}

		// Check up
		if (board.isOpen(x, y + 1)) {
			storage.store(new TraceState(board, x, y + 1));
		}

		// Check down
		if (board.isOpen(x, y - 1)) {
			storage.store(new TraceState(board, x, y - 1));
		}

		while (!storage.isEmpty()) {
			TraceState currentState = storage.retrieve();

			if (currentState.isSolution()) {
				if (bestPaths.isEmpty() || currentState.pathLength() == bestPaths.get(0).pathLength()) {
					bestPaths.add(currentState);
				} else if (currentState.pathLength() < bestPaths.get(0).pathLength()) {
					bestPaths.clear();
					bestPaths.add(currentState);
				}
			}

			else {
				x = currentState.getRow();
				y = currentState.getCol();

				if (currentState.isOpen(x - 1, y)) {
					storage.store(new TraceState(currentState, x - 1, y));
				}
				if (currentState.isOpen(x + 1, y)) {
					storage.store(new TraceState(currentState, x + 1, y));
				}
				if (currentState.isOpen(x, y - 1)) {
					storage.store(new TraceState(currentState, x, y - 1));
				}
				if (currentState.isOpen(x, y + 1)) {
					storage.store(new TraceState(currentState, x, y + 1));
				}
			}
		}

		// Output results to console or GUI, according to specified choice
		switch (args[1]) {
			case "-c":
				for (TraceState path : bestPaths) {
					System.out.println(path.getBoard().toString());
				}
				break;
			case "-g":
				System.out.println("GUI output not supported");
				break;
			default:
				printUsage();
				break;
		}
	}

} // class CircuitTracer
