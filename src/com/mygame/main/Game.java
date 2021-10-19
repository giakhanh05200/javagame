package com.mygame.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Random;


public class Game extends Canvas implements Runnable{
	
	
	private static final long serialVersionUID = -3480268084120731011L;
	
	//declare some variables and Thread
	public static final int WIDTH = 640, HEIGHT = WIDTH / 12 * 9;
	
	private Thread thread;
	private boolean running; 
	
	private Random r;
	private Handler handler;
	private HUD hud;
	
	//initial our game 
	public Game() {
		handler = new Handler();

		this.addKeyListener(new KeyInput(handler));
		
		new Window(WIDTH, HEIGHT, "Let's make a game!", this);
		
		hud = new HUD();
		
		r = new Random();
		
		handler.addObject(new Player(WIDTH/2-32, HEIGHT/2-32, ID.Player, handler));
		//handler.addObject(new Player(WIDTH/2+64, HEIGHT/2-32, ID.Player2));
		handler.addObject(new BasicEnemy(r.nextInt(WIDTH), r.nextInt(HEIGHT), ID.BasicEnemy, handler));
		handler.addObject(new BasicEnemy(r.nextInt(WIDTH), r.nextInt(HEIGHT), ID.BasicEnemy, handler));


	}
	
	//start constructor for our game.start()
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	//stop constructor for our game.stop()
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//most important, heartbeat of our game this is game loop 
	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta --;
			}
			if(running)
				render();
			frames++;
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				//System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
		
	}
	
	private void tick() {
		handler.tick();
		hud.tick();
	}
	
	//render methods that create 3 buffers
	//and create a black background for our frame game 
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.black);
		g.fillRect(0,0, WIDTH, HEIGHT);
		
		handler.render(g);
		hud.render(g);

		g.dispose();
		bs.show();
	}
	
	public static int clamp(int var, int min, int max) {
		if(var >= max)
			return var = max;
		else if(var <= min)
			return var = min;
		else
			return var;
	}
	
	public static void main(String args[]) {
		new Game();
	}

}
