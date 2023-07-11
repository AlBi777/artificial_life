package com.mycompany.world;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.JavaBean;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.swing.*;


public class World extends JFrame{
    public int status = 0;
    public int clearf = 0;
    public int width;
    public int height;
    public int speed;
    public int viewMode = 0;
    public int xw =180;
    public int yh =40;
    public Bot[][] matrix;
    public int generation; // Поколение
    public int population; //
    public int organic; //
    public int sunEater;
    public int predator;
    public int minEater;
    public int sizebot =15;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.matrix = new Bot[width][height];

        simulation = this;

        setTitle ("Искусственая жизнь 1.0");
        setSize (new Dimension (840, 460));
        Dimension sSize = Toolkit.getDefaultToolkit ().getScreenSize (), fSize = getSize ();
        getContentPane().setLayout(null);
        if (fSize.height > sSize.height) {fSize.height = sSize.height;}
        if (fSize.width  > sSize.width)  {fSize.width = sSize.width;}
        setLocation ((sSize.width - fSize.width)/2, (sSize.height - fSize.height)/2);

        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        setVisible (true);

        JSlider jSlider1 = new JSlider();
        jSlider1.setLocation(19, 270);
        jSlider1.setSize(140, 50);
        jSlider1.setMaximum(10);
        jSlider1.setMinimum(1);
        jSlider1.setMinorTickSpacing(1);
        jSlider1.setPaintTicks(true);
        jSlider1.setSnapToTicks(true);
        
        jSlider1.setToolTipText("Скорость");
       jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                speed = jSlider1.getValue();
            }
        });
        add(jSlider1);
        //Кнопки

        JButton starting = new JButton();
        if (status == 0)starting.setText("Старт");
        else starting.setText("Стоп");
        starting.setLocation(19,245);
        starting.setSize(140,20);
        ActionListener start = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (status == 0){
                    status = 1;viewMode = 0;
                    speed = jSlider1.getValue();
                    starting.setText("Стоп");
                }
                else {
                    status = 0;speed = jSlider1.getValue();
                    starting.setText("Старт");}
            }
        };
        starting.addActionListener(start);
        add(starting);
        
        
        JButton restarting = new JButton("Запустить заново");
        restarting.setVisible(true);
        restarting.setLocation(19,220);
        restarting.setSize(140,20);
        ActionListener restart = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        matrix[x][y] = null;}}
                generateFirstBot();
                speed = jSlider1.getValue();
                status = 0;viewMode = 0;
                 starting.setText("Старт");}
                
                
        };
        restarting.addActionListener(restart);
        add(restarting);
        Box p = Box.createVerticalBox();
        ButtonGroup bg = new ButtonGroup();
        JRadioButton rButton1 = new JRadioButton("Обычный");
        JRadioButton rButton2 = new JRadioButton("Жизнь/Здоровье");
        JRadioButton rButton3 = new JRadioButton("Минералы");
        JRadioButton rButton4 = new JRadioButton("Роли");
        bg.add(rButton1);
        bg.add(rButton2);
        bg.add(rButton3);
        bg.add(rButton4);
        
        p.add(rButton1);
        p.add(rButton2);
        p.add(rButton3);
        p.add(rButton4);
        
        rButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewMode = 0;
            }
        });
        rButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewMode = 1;
            }
        });
        rButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewMode = 2;
            }
        });
        rButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewMode = 3;
            }
        });
        
        
        p.setLocation(29, 320);
        p.setSize(140,90);
        add(p);
        
        
        JSlider jSlider2 = new JSlider();
        jSlider2.setLocation(19, 430);
        jSlider2.setSize(140, 50);
        jSlider2.setMaximum(20);
        jSlider2.setMinimum(5);
        jSlider2.setValue(10);
        jSlider2.setMinorTickSpacing(1);
        jSlider2.setPaintTicks(true);
        jSlider2.setSnapToTicks(true);
        jSlider2.setPaintLabels(true);
        jSlider2.setInverted(true);
        jSlider2.setToolTipText("Размер клетки");
       jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizebot = jSlider2.getValue();
                clearf =1;
            }
        });
        add(jSlider2);
        
        
    }

    @Override
    public void paint(Graphics g)  {
       if(clearf == 1){ clearf = 0;
        g.setColor(new Color(242,242,242));
        g.fillRect(0, 0, 1920, 1024);
       }
        g.drawRect(xw-1, yh-1, simulation.width * sizebot + 1, simulation.height * sizebot + 1);
g.setFont(new java.awt.Font("Segoe UI", 0, 7));
        population = 0;
        organic = 0;
        sunEater = 0;
        predator = 0;
        minEater = 0;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                
                if (matrix[x][y] == null) {
                
                        g.setColor(Color.WHITE);
                        g.fillRect(xw + x * sizebot, yh + y * sizebot, sizebot, sizebot);

                        if (y > World.simulation.height / 2) {
                            g.setColor(new Color(123, 172, 246));
                            g.fillRect(xw + x * sizebot, yh + y * sizebot, sizebot, sizebot);

                        }
                        if (y > World.simulation.height / 6 * 4) {
                            g.setColor(new Color(45, 106, 197));
                            g.fillRect(xw + x * sizebot, yh + y * sizebot, sizebot, sizebot);
                            
                        }
                        if (y > World.simulation.height / 6 * 5) {
                            g.setColor(new Color(9, 55, 126));
                            g.fillRect(xw + x * sizebot, yh + y * sizebot, sizebot, sizebot);

                     }


                    } else if ((matrix[x][y].alive == 1) || (matrix[x][y].alive == 2)) {
                        g.setColor(new Color(200, 200, 200));
                        g.fillRect(xw + x * sizebot, yh + y * sizebot, sizebot, sizebot);
                        organic = organic + 1;
                    } else if (matrix[x][y].alive == 3) {
                        g.setColor(Color.WHITE);
                        g.fillRect(xw + x * sizebot, yh + y * sizebot, sizebot, sizebot);
                        
                        int green = (int) (matrix[x][y].c_green - ((matrix[x][y].c_green * matrix[x][y].health) / 2000));
                        if (green < 0) green = 0;
                        if (green > 255) green = 255;
                        int blue = (int) (matrix[x][y].c_blue * 0.8 - ((matrix[x][y].c_blue * matrix[x][y].mineral) / 2000));

                        g.setColor(new Color(matrix[x][y].c_red, green, blue));
                        int f =1;
                        if (matrix[x][y].c_green >= matrix[x][y].c_blue && matrix[x][y].c_green >= matrix[x][y].c_red){sunEater = sunEater + 1;f=1;}
                        if (matrix[x][y].c_red > blue && matrix[x][y].c_green < matrix[x][y].c_red){predator = predator + 1;f=2;}
                        if (matrix[x][y].c_blue > matrix[x][y].c_green && matrix[x][y].c_blue > matrix[x][y].c_red){minEater = minEater + 1;f=3;}
                        
                        if(viewMode == 0)g.fillRect(xw + x * sizebot, yh + y * sizebot, sizebot, sizebot);
                        
                        if(viewMode == 1)g.drawString(String.valueOf(simulation.matrix[x][y].health), xw + x * sizebot, yh + y * sizebot+sizebot-5);
                        
                        if(viewMode == 2)g.drawString(String.valueOf(simulation.matrix[x][y].health), xw + x * sizebot, yh + y * sizebot+sizebot-5);
                        
                        if(viewMode == 3){
                            if(f == 1){g.setColor(Color.green);}
                            if(f == 2){g.setColor(Color.red);}
                            if(f == 3){g.setColor(Color.blue);}
                        
                            g.fillRect(xw + x * sizebot, yh + y * sizebot, sizebot, sizebot);
                        }
                        
                        
                        
                        
                        population = population + 1;
                    
                }
                
                }
            }
        
g.setFont(new java.awt.Font("Segoe UI", 0, 13));
        g.drawRect(24, 39, 141,200 );
        g.setColor(Color.WHITE);
        g.fillRect(25, 40, 140, 16);
        g.setColor(Color.BLACK);
        g.drawString("Поколение: " + String.valueOf(generation), 29, 54);

        g.setColor(Color.WHITE);
        g.fillRect(25, 60, 140, 16);
        g.setColor(Color.BLACK);
        g.drawString("Популяция: " + String.valueOf(population), 29, 74);

        g.setColor(Color.WHITE);
        g.fillRect(25, 80, 140, 16);
        g.setColor(Color.BLACK);
        g.drawString("Органика: " + String.valueOf(organic), 29, 94);

        g.setColor(Color.WHITE);
        g.fillRect(25, 100, 140, 16);
        g.setColor(Color.BLACK);
        g.drawString("Фотосинтезиты: " + String.valueOf(sunEater), 29, 114);

        g.setColor(Color.WHITE);
        g.fillRect(25, 120, 140, 16);
        g.setColor(Color.BLACK);
        g.drawString("Хищники: " + String.valueOf(predator), 29, 134);

        g.setColor(Color.WHITE);
        g.fillRect(25, 140, 140, 16);
        g.setColor(Color.BLACK);
        g.drawString("Минеральные к.: " + String.valueOf(minEater), 29, 154);

        // Минеральные зоны
        g.setColor(Color.WHITE);
        g.fillRect(25, 160, 140, 16);
        g.setColor(Color.BLACK);
        g.drawString("Минеральные зоны: ", 29, 174);

        g.setColor(new Color(123, 172, 246));
        g.fillRect(25, 180, 140, 16);
        g.setColor(Color.WHITE);
        g.drawString("Минералы +1 ", 29, 194);

        g.setColor(new Color(45, 106, 197));
        g.fillRect(25, 200, 140, 16);
        g.setColor(Color.WHITE);
        g.drawString("Минералы +2 ", 29, 214);

        g.setColor(new Color(9, 55, 126));
        g.fillRect(25, 220, 140, 16);
        g.setColor(Color.white);
        g.drawString("Минералы +3 ", 29, 234);


    }
    
    
    // Основной цикл ------------------------------------------------------------------------
    public void run() {


        while (true) {

            if (status == 1){
            //Обновляем матрицу мира
                for (int yw = 0; yw < height; yw++) {
                    for (int xw = 0; xw < width; xw++) {
                        if (matrix[xw][yw] != null) {
                            matrix[xw][yw].step();

                        }
                    }
                }
                //if (population> 600)sleep(10,1);
                generation = generation + 1;
              //  sleep(100,speed);
            }

            paint(getGraphics());        //отображаем текущее состояние симуляции на экран
        }
    }



    public static World simulation;

    public static void main(String[] args) {

       simulation = new World(70, 60);
       simulation.generateFirstBot();// завязать на кнопке
       
        simulation.run();
    }

    // делает временную паузу //или просто замедление
    public void sleep(int millisecond ,int x) {
        try {
            int delay = millisecond;
            delay = delay /x;

            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
        }
    }

    public void generateFirstBot() {
        Bot bot = new Bot();
        generation = 0;
        bot.adr = 0;
        bot.x = width / 2;       // координаты бота
        bot.y = height / 2;
        bot.health = 990;      // энергия
        bot.mineral = 0;        // минералы
        bot.alive = 3; // отмечаем, что бот живой
        bot.c_red = 170;  // задаем цвет бота
        bot.c_blue = 170;
        bot.c_green = 171;
        bot.direction = 5;        // направление
        bot.mprev = null;bot.mnext = null;
        for (int i = 0; i < 64; i++) {
            bot.mind[i] = 25;
        }
viewMode = 0;
        matrix[bot.x][bot.y] = bot;
        return;
    }


}
