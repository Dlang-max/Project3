import java.util.*;
import java.util.function.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ParticleSimulator extends JPanel {
	private Heap<Event> _events;
	private java.util.List<Particle> _particles;
	private double _duration;
	private int _width;

	/**
	 * @param filename the name of the file to parse containing the particles
	 */
	public ParticleSimulator (String filename) throws IOException {
		_events = new HeapImpl<>();

		// Parse the specified file and load all the particles.
		Scanner s = new Scanner(new File(filename));
		_width = s.nextInt();
		_duration = s.nextDouble();
		s.nextLine();
		_particles = new ArrayList<>();
		while (s.hasNext()) {
			String line = s.nextLine();
			Particle particle = Particle.build(line);
			_particles.add(particle);
		}

		setPreferredSize(new Dimension(_width, _width));
	}

	@Override
	/**
	 * Draws all the particles on the screen at their current locations
	 * DO NOT MODIFY THIS METHOD
	 */
        public void paintComponent (Graphics g) {
		g.clearRect(0, 0, _width, _width);
		for (Particle p : _particles) {
			p.draw(g);
		}
	}

	// Helper class to signify the final event of the simulation.
	private class TerminationEvent extends Event {
		TerminationEvent (double timeOfEvent) {
			super(timeOfEvent, 0);
		}
	}

	/**
	 * Helper method to update the positions of all the particles based on their current velocities.
	 */
	private void updateAllParticles (double delta) {
		for (Particle p : _particles) {
			p.update(delta);
			
		}
	}

	/**
	 * Executes simulation.
	 */
	private void simulate (boolean show) {
		double lastTime = 0;

		// Create initial events, i.e., all the possible
		// collisions between all the particles and each other,
		// and all the particles and the walls.
		enqueueInitialEvents();
		_events.add(new TerminationEvent(_duration));

		//Simulation loop. Runs until _duration is over or there are no future collisions.
		while (_events.size() > 0) {
			Event event = _events.removeFirst();
			double delta = event._timeOfEvent - lastTime;

			if (event instanceof TerminationEvent) {
				updateAllParticles(delta);
				break;
			}

			//Check if event still valid; if not, then skip this event
			if (isNotValidEvent(event)) {
				continue;
			}

			// Since the event is valid, then pause the simulation for the right
			// amount of time, and then update the screen.
			if (show) {
				try {
					Thread.sleep((long) delta * 100);
				} catch (InterruptedException ie) {}
			}

			// Update positions of all particles
			updateAllParticles(delta);
			
			// Update the velocity of the particle(s) involved in the collision
			// (either for a particle-wall collision or a particle-particle collision).
			// You should call the Particle.updateAfterCollision method at some point.
			if(event._p2 != null) {
				event._p1.updateAfterCollision(event._timeOfEvent, event._p2);
			}
			else {
				event._p1.updateAfterWallCollision(event._timeOfEvent, _width, _width);
			}

			//Check for collisions with other particles
			enqueueParticleCollisionsAfterCollision(event);

			//Check for collisions with walls
			enqueueParticleWallCollisions(event);

			// Update the time of our simulation
			lastTime = event._timeOfEvent;

			// Redraw the screen
			if (show) {
				repaint();
			}
		}

		// Print out the final state of the simulation
		System.out.println(_width);
		System.out.println(_duration);
		for (Particle p : _particles) {
			System.out.println(p);
		}
	}


	/**
	 * Adds the initial collisions predicted for all the particles at the initial time to the heap.
	 */
	private void enqueueInitialEvents () {
		for(int i = 0; i < _particles.size(); i++) {
			for(int j = i + 1; j < _particles.size(); j++) {
				double collisionTime = _particles.get(i).getCollisionTime(_particles.get(j));
				if(collisionTime < Double.POSITIVE_INFINITY) {
					_events.add(new Event(collisionTime, 0.0, _particles.get(i), _particles.get(j)));
				}
			}
			double wallCollisionTime = _particles.get(i).getWallCollisionTime(_width, _width);
			if(wallCollisionTime < Double.POSITIVE_INFINITY){
				_events.add(new Event(wallCollisionTime, 0.0, _particles.get(i)));
			}
		}
	}

	/**
	 * Tests if an event is not valid.
	 * 
	 * @param event the event being validated
	 * @return true if event is invalid
	 */
	private boolean isNotValidEvent (Event event) {
		return event._p1.get_lastUpdateTime() > event._timeEventCreated || (event._p2 != null && event._p2.get_lastUpdateTime() > event._timeEventCreated);
	}
	
	/**
	 * Calculates the next particle-particle collision for the particle(s) involved in an event.
	 * 
	 * @param event the event afterwhich to check for new collisions.
	 */
	private void enqueueParticleCollisionsAfterCollision(Event event) {
		for(Particle p : _particles){
				if(!p.equals(event._p1)){
					double time = event._p1.getCollisionTime(p);
					if(time < Double.POSITIVE_INFINITY){
						_events.add(new Event(time + event._timeOfEvent, event._timeOfEvent, event._p1, p));
					}
				}
				if(event._p2 != null && !p.equals(event._p2)){
					double time = event._p2.getCollisionTime(p);
					if(time < Double.POSITIVE_INFINITY){
						_events.add(new Event(time + event._timeOfEvent, event._timeOfEvent, event._p2, p));
					}
				}
			}
	}
	
	/**
	 * Calculates the next wall collision for the particle(s) involved in an event.
	 * 
	 * @param event the event afterwhich to check for new collisions.
	 */
	private void enqueueParticleWallCollisions(Event event) {
		double time = event._p1.getWallCollisionTime(_width, _width);
		if(time < Double.POSITIVE_INFINITY){
			_events.add(new Event(time + event._timeOfEvent, event._timeOfEvent, event._p1));
		}

		if(event._p2 != null){
			time = event._p2.getWallCollisionTime(_width, _width);
			if(time < Double.POSITIVE_INFINITY){
				_events.add(new Event(time + event._timeOfEvent, event._timeOfEvent, event._p2));
			}
		}
	}


	public static void main (String[] args) throws IOException {
		
		//For testing:
		// args = new String[1];
		// args[0] = "particles_b_start.txt";

				
		if (args.length < 1) {
			System.out.println("Usage: java Particle Simulator <filename>");
			System.exit(1);
		}

		ParticleSimulator simulator;

		simulator = new ParticleSimulator(args[0]);
		JFrame frame = new JFrame();
		frame.setTitle("Particle Simulator");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(simulator, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		simulator.simulate(true);
	}

	/**
	 * Tests the result of a simulation run against a list of expected values. (Encapsulated testing);
	 * 
	 * @param results a list containing expected results for width, duration and particle states
	 * @return true if the results match the expected
	 */
	public boolean testParticleSimulator(ArrayList<String> results){
		simulate(false);
		if(!String.valueOf(_width).equals(results.get(0))) return false;
		if(!String.valueOf(_duration).equals(results.get(1))) return false;
		if(_particles.size() != results.size() -2) return false;
		for(int i = 0; i < _particles.size(); i++){
			if(!_particles.get(i).toString().equals(results.get(i+2))) return false;
		}

		return true;
	}		
}	