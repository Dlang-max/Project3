import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.ArrayList;

public class ParticleSimulatorTester {
    
    //Particles Colliding with Walls
    Particle p1 = new Particle("p1", 30, 50, -5, 0, 5);
    Particle p2 = new Particle("p2", 465, 50, 5, 0, 5);
    Particle p3 = new Particle("p3", 50, 30, 0, -5, 5);
    Particle p4 = new Particle("p4", 50, 465, 0, 5, 5);

    //Particles Colliding with Each Other
    Particle p5 = new Particle("p5", 30, 50, 5, 0, 5);
    Particle p6 = new Particle("p6", 60, 50, -5, 0, 5);

    Particle p7 = new Particle("p7", 40, 50, 5, 0, 5);
    Particle p8 = new Particle("p8", 50, 50, -5, 0, 5);

    Particle p9 = new Particle("p9", 40, 40, 5, 5, 5);
    Particle p10 = new Particle("p10", 50, 50, -5, -5, 5);

    Particle p11 = new Particle("p11", 43, 43, 5, 5, 5);
    Particle p12 = new Particle("p12", 50, 50, -5, -5, 5);


	@Test
    void testParticleSideWallsCollisionTime(){
        //Left Wall
        double collisionTime = p1.getWallCollisionTime(500, 500);
        assertEquals(collisionTime, 5.0);
        
        //Right Wall
        collisionTime = p2.getWallCollisionTime(500, 500);
        assertEquals(collisionTime, 6.0);
    }

    @Test
    void testParticleTopBottomWallCollisionTime(){
        //Top Wall
        double collisionTime = p3.getWallCollisionTime(500, 500);
        assertEquals(collisionTime, 5.0);
        
        //Bottom Wall
        collisionTime = p4.getWallCollisionTime(500, 500);
        assertEquals(collisionTime, 6.0);
    }

    @Test
    void testParticleParticleCollisionTime(){
        double collisionTime = p5.getCollisionTime(p6);
        assertEquals(collisionTime, 2);
    }

    @Test
    void testHeadOnParticleCollision(){
        p7.updateAfterCollision(0, p8);
        assertEquals(p7._vx, -5);
        assertEquals(p7._vy, 0);
        assertEquals(p8._vx, 5);
        assertEquals(p8._vy, 0);

        p9.updateAfterCollision(0, p10);
        assertEquals(p9._vx, -5);
        assertEquals(p9._vy, -5);
        assertEquals(p10._vx, 5);
        assertEquals(p10._vy, 5);
    }

    @Test
    void testParticleCollision(){

        double initialXVelocity = p11._vx + p12._vx;
        double initialYVelocity = p11._vy + p12._vy;
        double intialMagnitude = Math.sqrt(Math.pow(initialXVelocity, 2) + Math.pow(initialYVelocity, 2));

        p11.updateAfterCollision(0, p12);

        double finalXVelocity = p11._vx + p12._vx;
        double finalYVelocity = p11._vy + p12._vy;
        double finalMagnitude = Math.sqrt(Math.pow(finalXVelocity, 2) + Math.pow(finalYVelocity, 2));

        assertEquals(finalMagnitude, intialMagnitude);
    }

    @Test
    void testParticlesA() throws IOException{
        ArrayList<String> results = new ArrayList<String>();
        results.add("1000");
        results.add("100.0");
        results.add("p0 654.0401639768177  473.1675898901214 -1.568233700488868 0.522526102249389 5.0");
        results.add("p1 245.38410465936232  427.1841178425128 0.9956170542906531 3.8885936312832525 5.0");
        results.add("p2 45.94463727438902  644.7313984570708 6.922271946756815 3.130555300074951 5.0");
        
        ParticleSimulator simulator = new ParticleSimulator("particles_a_start.txt");
        assertTrue(simulator.testParticleSimulator(results));		
    }

    @Test
    void testParticlesB() throws IOException{
        ArrayList<String> results = new ArrayList<String>();
        results.add("100");
        results.add("100.0");
        results.add("p0 75.3893241184703  40.735423878567914 -1.8312953367190943 3.0361562368245867 10.0");
        results.add("p1 75.8625154330703  13.448369137802224 8.79328612686825 -0.9027546511876512 10.0");
        results.add("p2 15.212254520849797  38.3375114053905 -5.822420645036839 -0.6250469249075055 10.0");
        
        ParticleSimulator simulator = new ParticleSimulator("particles_b_start.txt");
        assertTrue(simulator.testParticleSimulator(results));		
    }
}
