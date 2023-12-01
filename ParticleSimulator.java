import java.util.*;
import java.util.function.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.sound.sampled.*;

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
	 * Executes the actual simulation.
	 */
	private void simulate (boolean show) {
		double lastTime = 0;

		// Create initial events, i.e., all the possible
		// collisions between all the particles and each other,
		// and all the particles and the walls.
		for(int i = 0; i < _particles.size(); i++) {
			for(int j = i + 1; j < _particles.size(); j++) {
				double collisionTime = _particles.get(i).getCollisionTime(_particles.get(j));
				if(collisionTime < Double.POSITIVE_INFINITY) {
					System.out.println("Initial Collision: " + _particles.get(i) + " : " + _particles.get(j));
					System.out.println("Time: " + collisionTime);

					_events.add(new Event(collisionTime, lastTime, _particles.get(i), _particles.get(j)));
				}
			}
			double wallCollisionTime = _particles.get(i).getWallCollisionTime(_width, _width);
			if(wallCollisionTime < Double.POSITIVE_INFINITY){
				System.out.println("Initial Wall Collision: " + _particles.get(i));
				System.out.println("Time: " + wallCollisionTime);

				_events.add(new Event(wallCollisionTime, lastTime, _particles.get(i), null));
			}
		}
		
		_events.add(new TerminationEvent(_duration));


		int count = 0;
		while (_events.size() > 0 && count < 10) {
			Event event = _events.removeFirst();
			System.out.println("Current Time: " + event._timeOfEvent);
			double delta = event._timeOfEvent - lastTime;
			System.out.println("Delta: " + delta);
			System.out.println("Events: " + _events.size());

			//System.out.println("P1: " + event._p1._name);
			if(event._p2 != null){
				System.out.println("P2: " + event._p2._name);
			}



			if (event instanceof TerminationEvent) {
				updateAllParticles(delta);
				System.out.println("Terminated");
				break;
			}

			//Check if event still valid; if not, then skip this event
			if (event._p1.get_lastUpdateTime() > event._timeEventCreated || (event._p2 != null && event._p2.get_lastUpdateTime() > event._timeEventCreated)) {
				System.out.println("Skipping event" + event._timeEventCreated);
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
			System.out.println("Updating particles");


			// Update the velocity of the particle(s) involved in the collision
			// (either for a particle-wall collision or a particle-particle collision).
			// You should call the Particle.updateAfterCollision method at some point.
			if(event._p2 == null) {
				event._p1.updateAfterWallCollision(event._timeOfEvent, _width, _width);
				System.out.println("X: " + event._p1._x);
				System.out.println("Y: " + event._p1._y);
			}
			else {
				event._p1.updateAfterCollision(event._timeOfEvent, event._p2);
			}

			//Calculating new collisions:

			//Check for collisions with other particles
			for(Particle p : _particles){
				if(!p.equals(event._p1)){
					System.out.println("Enqueueing new event \n\n");
					double time = event._p1.getCollisionTime(p);
					if(time < Double.POSITIVE_INFINITY){
						_events.add(new Event(time + event._timeOfEvent, event._timeOfEvent, event._p1, p));
					}
				}
				if(event._p2 != null && !p.equals(event._p2)){
					System.out.println("Enqueueing new event \n\n");
					double time = event._p2.getCollisionTime(p);
					if(time < Double.POSITIVE_INFINITY){
						_events.add(new Event(time + event._timeOfEvent, event._timeOfEvent, event._p2, p));
					}
				}
			}


			//Check for collisions with walls
			double time = event._p1.getWallCollisionTime(_width, _width);
			if(time < Double.POSITIVE_INFINITY){
				System.out.println("Time!!!" + time);
				System.out.println("Queuing Wall Collision: ");
				_events.add(new Event(time + event._timeOfEvent, event._timeOfEvent, event._p1, null));
			}

			if(event._p2 != null){
				time = event._p2.getWallCollisionTime(_width, _width);
				if(time < Double.POSITIVE_INFINITY){
					_events.add(new Event(time + event._timeOfEvent, event._timeOfEvent, event._p2, null));
				}
			}

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
	public static void main (String[] args) throws IOException {
		
		//For testing:
		args = new String[1];
		args[0] = "particles_b_start.txt";

				
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
}