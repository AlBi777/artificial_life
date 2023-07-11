/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.world;

public class Bot {
    // Параметры
    public int adr;
    public int x;
    public int y;
    public int health;
    public int mineral;
    public int alive;
    public int c_red;
    public int c_green;
    public int c_blue;
    public int direction;
    public Bot mprev;
    public Bot mnext;

    public int[] mind = new int[64]; // Геном бота содержит 64 команды
    public int MIND_SIZE = 64; //Объем генома

    // Состояния
    public int LV_FREE = 0;  // место свободно
    public int LV_ORGANIC_HOLD = 1;  // бот погиб и стал органикой в подвешенном состоянии
    public int LV_ORGANIC_SINK = 2;  // органика начинает тонуть, пока не встретит препятствие, после чего остается в подвешенном состоянии(LV_ORGANIC_HOLD)
    public int LV_ALIVE = 3;  // живой бот


    public Bot() {
        direction = 2;
        health = 5;
        alive = 3;
    }

    // step - главная функция, в которой заложены правила поведения
    // пусто - 2, стена - 3, органик - 4, бот -5, родня - 6.
    public void step() {
        if (alive == 0 || alive == 1 || alive == 2) return;

        for (int cyc = 0; cyc < 15; cyc++) {
            int command = mind[adr];

            //Сменить направление относительно
            if (command == 23) {
                int param = botGetParam(this) % 8;
                int newdrct = direction + param;
                if (newdrct >= 8) {
                    newdrct = newdrct - 8;
                }
                direction = newdrct;
                IncCommandAddress(this, 2);
            }

            //Сменить направление абсолютно
            if (command == 24) {
                direction = botGetParam(this) % 8;
                IncCommandAddress(this, 2);
            }

            //Фотосинтез
            if (command == 25) {
                botEatSun(this);
                IncCommandAddress(this, 1);
                break;
            }

            //Шаг в относительном напралении
            if (command == 26) {
                if (isMulti(this) == 0) {
                    int drct = botGetParam(this) % 8;
                    inderectIncCommandAddres(this, botMove(this, drct, 0));
                } else {
                    IncCommandAddress(this, 2);
                }
                break;
            }

            //Шаг в абсолютном направлении
            if (command == 27) {
                if (isMulti(this) == 0) {
                    int drct = botGetParam(this) % 8;
                    inderectIncCommandAddres(this, botMove(this, drct, 1));
                }
                break;
            }

            //Съесть в относительном напралении
            if (command == 28) {
                int drct = botGetParam(this) % 8;
                inderectIncCommandAddres(this, botEat(this, drct, 0));
                break;
            }

            //Съесть в абсолютном направлении
            if (command == 29) {
                int drct = botGetParam(this) % 8;
                inderectIncCommandAddres(this, botEat(this, drct, 1));
                break;
            }

            //Посмотреть в относительном направлении
            if (command == 30) {
                int drct = botGetParam(this) % 8;
                inderectIncCommandAddres(this, botSeeBots(this, drct, 0));
            }

            //Посмотреть в абсолютном направлении
            if (command == 31)
            {
                int drct = botGetParam(this) % 8;
                inderectIncCommandAddres(this, botSeeBots(this, drct, 1));
            }

            //Делится в относительном напралении
            if ((command == 32) || (command == 42)) {
                int drct = botGetParam(this) % 8;
                inderectIncCommandAddres(this, botCare(this, drct, 0));
            }

            //Делится в абсолютном направлении.
            if ((command == 33) || (command == 50)) {
                int drct = botGetParam(this) % 8;
                inderectIncCommandAddres(this, botCare(this, drct, 1));
            }

            //Отдать в относительном напралении
            if ((command == 34) || (command == 51)) {
                int drct = botGetParam(this) % 8;
                inderectIncCommandAddres(this, botGive(this, drct, 0));
            }

            //Отдать в абсолютном направлении
            if ((command == 35) || (command == 52)) {
                int drct = botGetParam(this) % 8;
                inderectIncCommandAddres(this, botGive(this, drct, 1));
            }

            //Выравнится по горизонтали
            if (command == 36) {
                if (Math.random() < 0.5) {
                    direction = 3;
                } else {
                    direction = 7;
                }
                IncCommandAddress(this, 1);
            }

            //На какой высоте бот
            if (command == 37) {
                int param = botGetParam(this) * World.simulation.height / MIND_SIZE;
                if (y < param) {
                    inderectIncCommandAddres(this, 2);
                } else {
                    inderectIncCommandAddres(this, 3);
                }
            }

            //Здоровье бота
            if (command == 38) {
                int param = botGetParam(this) * 1000 / MIND_SIZE;
                if (health < param) {
                    inderectIncCommandAddres(this, 2);
                } else {
                    inderectIncCommandAddres(this, 3);
                }
            }

            //Сколько минералов
            if (command == 39) {
                int param = botGetParam(this) * 1000 / MIND_SIZE;
                if (mineral < param) {
                    inderectIncCommandAddres(this, 2);
                } else {
                    inderectIncCommandAddres(this, 3);
                }
            }

            //Многоклеточность (создание потомка)
            if (command == 40) {
                // функция isMulti() возвращает
                // 0 - если бот не входит в многоклеточную цепочку
                // 1 или 2 - если бот является крайним в цепочке
                // 3 - если бот внутри цепочки
                    int a = isMulti(this);    // 0 - нету, 1 - есть MPREV, 2 - есть MNEXT, 3 есть MPREV и MNEXT
                    if (a == 3) {
                       botDouble(this);
                    } else {    // если бот уже находится внутри цепочки, то новый бот рождается свободным
                        botMulti(this);
                  }
                  IncCommandAddress(this, 1);
                 break;
            }

            //Деление (создание свободного потомка)
            if (command == 41) {
                int a = isMulti(this);
                if ((a == 0) || (a == 3)) {
                    botDouble(this);  // если бот свободный или внутри цепочки, , то новый бот рождается свободным
                } else {
                    botMulti(this);  // если бот крайний в цепочке, новый бот рождается приклеенным к боту-предку
                }
                IncCommandAddress(this, 1);
                break;
            }

            //Окружение
            if (command == 43) {
                inderectIncCommandAddres(this, fullAroud(this));
            }

            //Прибавляется ли энергия
            if (command == 44) {  // is_health_grow() возвращает 1, если энегрия у бота прибавляется, иначе - 2
                inderectIncCommandAddres(this, isHealthGrow(this));
            }

            //Прибавляется ли кристалы
            if (command == 45) {
                if (y > (World.simulation.height / 2)) {
                    inderectIncCommandAddres(this, 1);
                } else {
                    inderectIncCommandAddres(this, 2);
                }
            }

            //Проверка на многоклеточность
            if (command == 46) {
                int mu = isMulti(this);
                if (mu == 0) {
                    inderectIncCommandAddres(this, 1); // бот свободно живущий
                } else {
                    if (mu == 3) {
                        inderectIncCommandAddres(this, 3); // внутри цепочки
                    } else {
                        inderectIncCommandAddres(this, 2); // начало или конец цепочки
                    }
                }
            }

            //Преобразовать минералы в энерию
            if (command == 47) {
                MineralToEnergy(this);
                IncCommandAddress(this, 1);
                break;
            }

            //Мутировать
            if (command == 48) {
                int ma = (int) (Math.random() * 64);  // 0..63
                int mc = (int) (Math.random() * 64);  // 0..63
                mind[ma] = mc;

                ma = (int) (Math.random() * 64);  // 0..63
                mc = (int) (Math.random() * 64);  // 0..63
                mind[ma] = mc;
                IncCommandAddress(this, 1);
                break;
            }

            //Генная атака
            if (command == 49) {
                botGenAttack(this);
                IncCommandAddress(this, 1);
                break;
            }
        }

        int command = mind[adr];
        if (((command >= 0) && (command <= 22)) || ((command >= 53) && (command <= 63))) {
            IncCommandAddress(this, command);
        }


        //Распределение энергии в многоклеточном организме
        if (alive == LV_ALIVE) {
            int a = isMulti(this);

            if (a == 3) {
                Bot pb = mprev;
                Bot nb = mnext;
                int m = mineral + nb.mineral + pb.mineral;
                m = m / 3;
                mineral = m;
                nb.mineral = m;
                pb.mineral = m;

                int apb = isMulti(pb);
                int anb = isMulti(nb);
                if ((anb == 3) && (apb == 3)) {
                    int h =  health + nb.health + pb.health;
                    h = h / 3;
                    health = h;
                    nb.health = h;
                    pb.health = h;
                }
            }

            if (a == 1) {
                Bot pb = mprev;
                int apb = isMulti(pb);
                if (apb == 3) {

                    int h =  health + pb.health;
                    h = h / 4;
                    health = h * 3;
                    pb.health = h;
                }
            }

            if (a == 2) {
                Bot nb = mnext;
                int anb = isMulti(nb);
                if (anb == 3) {

                    int h =  health + nb.health;
                    h = h / 4;
                    health = h * 3;
                    nb.health = h;
                }
            }

            if (health > 999) {
                if ((a == 1) || (a == 2)) {
                    botMulti(this);
                } else {
                    botDouble(this);
                }
            }
            health =  health - 3;
            if (health < 1) {
                botToOrganic(this);
                return;
            }

            if (y > World.simulation.height / 2) {
                mineral = mineral + 1;
                if (y > World.simulation.height / 6 * 4) { mineral = mineral + 1; }
                if (y > World.simulation.height / 6 * 5) { mineral = mineral + 1; }
                if (mineral > 999) { mineral = 999; }
            }
        }
    }

    //Получение Х-координаты рядом
    //по относительному направлению
    public int xFromVektorR(Bot bot, int n) {
        int xt = bot.x;
        n = n + bot.direction;
        if (n >= 8) {
            n = n - 8;
        }
        if (n == 0 || n == 6 || n == 7) {
            xt = xt - 1;
            if (xt == -1) {
                xt = World.simulation.width - 1;
            }
        } else if (n == 2 || n == 3 || n == 4) {
            xt = xt + 1;
            if (xt == World.simulation.width) {
                xt = 0;
            }
        }
        return xt;
    }

    //Получение Х-координаты рядом
    // по абсолютному направлению
    public int xFromVektorA(Bot bot, int n) {
        int xt = bot.x;
        if (n == 0 || n == 6 || n == 7) {
            xt = xt - 1;
            if (xt == -1) {
                xt = World.simulation.width - 1;
            }
        } else if (n == 2 || n == 3 || n == 4) {
            xt = xt + 1;
            if (xt == World.simulation.width) {
                xt = 0;
            }
        }
        return xt;
    }

    //Получение Y-координаты рядом
    //по относительному направлению
    public int yFromVektorR(Bot bot, int n) {
        int yt = bot.y;
        n = n + bot.direction;
        if (n >= 8) {
            n = n - 8;
        }
        if (n == 0 || n == 1 || n == 2) {
            yt = yt - 1;
        } else if (n == 4 || n == 5 || n == 6) {
            yt = yt + 1;
        }
        return yt;
    }

    //Получение Y-координаты рядом
    //по абсолютному направлению
    public int yFromVektorA(Bot bot, int n) {
        int yt = bot.y;
        if (n == 0 || n == 1 || n == 2) {
            yt = yt - 1;
        } else if (n == 4 || n == 5 || n == 6) {
            yt = yt + 1;
        }
        return yt;
    }

    //Окружен ли бот
    public int fullAroud(Bot bot) {
        for (int i = 0; i < 8; i++) {
            int xt = xFromVektorR(bot, i);
            int yt = yFromVektorR(bot, i);
            if ((yt >= 0) && (yt < World.simulation.height)) {
                if (World.simulation.matrix[xt][yt] == null) {
                    return 2;
                }
            }
        }
        return 1;
    }

    //Ищет свободные ячейки вокруг бота
    public int findEmptyDirection(Bot bot) {
        for (int i = 0; i < 8; i++) {
            int yt = yFromVektorR(bot, i);
            int xt = xFromVektorR(bot, i);
            if ((yt >= 0) && (yt < World.simulation.height)) {
                if (World.simulation.matrix[xt][yt] == null) {
                    return i;
                }
            }
        }
        return 8;
    }

    //Получение параметра для команды
    public int botGetParam(Bot bot) {
        int paramadr = bot.adr + 1;
        if (paramadr >= MIND_SIZE) {
            paramadr = paramadr - MIND_SIZE;
        }
        return bot.mind[paramadr]; // возвращает число, следующее за выполняемой командой
    }

    //Увеличение адреса команды
    public void IncCommandAddress(Bot bot, int a) {
        int paramadr = bot.adr + a;
        if (paramadr >= MIND_SIZE) {
            paramadr = paramadr - MIND_SIZE;
        }
        bot.adr = paramadr;
    }

    //Косвенное увеличение адреса команд
    public void inderectIncCommandAddres(Bot bot, int a) {
        int paramadr = bot.adr + a;
        if (paramadr >= MIND_SIZE) {
            paramadr = paramadr - MIND_SIZE;
        }
        int bias = bot.mind[paramadr];
        IncCommandAddress(bot, bias);
    }

    //Превращение бота в органику
    public void botToOrganic(Bot bot) {
        bot.alive = LV_ORGANIC_HOLD;
        Bot pbot = bot.mprev;
        Bot nbot = bot.mnext;
        if (pbot != null){ pbot.mnext = null; }
        if (nbot != null){ nbot.mprev = null; }
        bot.mprev = null;
        bot.mnext = null;
    }

    // Бот - часть организма?
    public int isMulti(Bot bot) {
        int a = 0;
        if (bot.mprev != null) {
            a = 1;
        }
        if (bot.mnext != null) {
            a = a + 2;
        }
        return a;
    }

    //Перемещение бота( страдательное )
    public void moveBot(Bot bot, int xt, int yt) {
        World.simulation.matrix[xt][yt] = bot;
        World.simulation.matrix[bot.x][bot.y] = null;
        bot.x = xt;
        bot.y = yt;
    }

    //Удаление бота
    public void deleteBot(Bot bot) {
        Bot pbot = bot.mprev;
        Bot nbot = bot.mnext;
        if (pbot != null){ pbot.mnext = null; }
        if (nbot != null){ nbot.mprev = null; }
        bot.mprev = null;
        bot.mnext = null;
        World.simulation.matrix[bot.x][bot.y] = null;
    }

    //Фотосинтез
    public void botEatSun(Bot bot) {
        int t;
        if (bot.mineral < 100) {
            t = 0;
        } else if (bot.mineral < 400) {
            t = 1;
        } else {
            t = 2;
        }
        int a = 0;
        if (bot.mprev != null) {
            a = a + 4;
        }
        if (bot.mnext != null) {
            a = a + 4;
        }
        int hlt = a + 1* (11 - (15 * bot.y / World.simulation.height) + t);
        if (hlt > 0) {
            bot.health = bot.health + hlt;
            goGreen(bot, hlt);
        }
    }

    //Преобразование минералов в энергию
    public void MineralToEnergy(Bot bot) {
        if (bot.mineral > 100) {
            bot.mineral = bot.mineral - 100;
            bot.health = bot.health + 400;
            goBlue(bot, 100);
        } else {
            goBlue(bot, bot.mineral);
            bot.health = bot.health + 4 * bot.mineral;
            bot.mineral = 0;
        }
    }

    //Перемещение бота
    public int botMove(Bot bot, int direction, int ra) {
        int xt;
        int yt;
        if (ra == 0) {
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if ((yt < 0) || (yt >= World.simulation.height)) {
            return 3;
        }
        if (World.simulation.matrix[xt][yt] == null) {
            moveBot(bot, xt, yt);
            return 2;
        }

        if (World.simulation.matrix[xt][yt].alive <= LV_ORGANIC_SINK) {
            return 4;
        }
        if (isRelative(bot, World.simulation.matrix[xt][yt]) == 1) {
            return 6;
        }
        return 5;
    }

    //Поглотить другого бота или органику
    public int botEat(Bot bot, int direction, int ra) {
        bot.health = bot.health - 4;
        int xt;
        int yt;
        if (ra == 0) {
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if ((yt < 0) || (yt >= World.simulation.height)) {
            return 3;
        }
        if (World.simulation.matrix[xt][yt] == null) {
            return 2;
        }

        else if (World.simulation.matrix[xt][yt].alive <= LV_ORGANIC_SINK) {
            deleteBot(World.simulation.matrix[xt][yt]);
            bot.health = bot.health + 100;
            goRed(this, 100);
            return 4;
        }

        int min0 = bot.mineral;
        int min1 = World.simulation.matrix[xt][yt].mineral;
        int hl = World.simulation.matrix[xt][yt].health;
        if (min0 >= min1) {
            bot.mineral = min0 - min1;
            deleteBot(World.simulation.matrix[xt][yt]);
            int cl = 100 + (hl / 2);
            bot.health = bot.health + cl;
            goRed(this, cl);
            return 5;
        }

        bot.mineral = 0;
        min1 = min1 - min0;
        World.simulation.matrix[xt][yt].mineral = min1 - min0;
        if (bot.health >= 2 * min1) {
            deleteBot(World.simulation.matrix[xt][yt]);
            int cl = 100 + (hl / 2) - 2 * min1;
            bot.health = bot.health + cl;
            if (cl < 0) { cl = 0; }
            goRed(this, cl);
            return 5;
        }

        World.simulation.matrix[xt][yt].mineral = min1 - (bot.health / 2);
        bot.health = 0;
        return 5;
    }

    //Посмотреть
    public int botSeeBots(Bot bot, int direction, int ra) {
        int xt;
        int yt;
        if (ra == 0) {
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if (yt < 0 || yt >= World.simulation.height) {
            return 3;
        } else if (World.simulation.matrix[xt][yt] == null) {
            return 2;
        } else if (World.simulation.matrix[xt][yt].alive <= LV_ORGANIC_SINK) {
            return 4;
        } else if (isRelative(bot, World.simulation.matrix[xt][yt]) == 1) {
            return 6;
        } else {
            return 5;
        }
    }


    //Атака на ген соседа, меняем случайны ген случайным образом
    public void botGenAttack(Bot bot) {
        int xt = xFromVektorR(bot, 0);
        int yt = yFromVektorR(bot, 0);
        if ((yt >= 0) && (yt < World.simulation.height) && (World.simulation.matrix[xt][yt] != null)) {
            if (World.simulation.matrix[xt][yt].alive == LV_ALIVE) {
                bot.health = bot.health - 10;
                if (bot.health > 0) {
                    int ma = (int) (Math.random() * 64);
                    int mc = (int) (Math.random() * 64);
                    World.simulation.matrix[xt][yt].mind[ma] = mc;
                }
            }
        }
    }

    //Поделится
    public int botCare(Bot bot, int direction, int ra) {
        int xt;
        int yt;
        if (ra == 0) {
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if (yt < 0 || yt >= World.simulation.height) {
            return 3;
        } else if (World.simulation.matrix[xt][yt] == null) {
            return 2;
        } else if (World.simulation.matrix[xt][yt].alive <= LV_ORGANIC_SINK) {
            return 4;
        }

        int hlt0 = bot.health;
        int hlt1 = World.simulation.matrix[xt][yt].health;
        int min0 = bot.mineral;
        int min1 = World.simulation.matrix[xt][yt].mineral;
        if (hlt0 > hlt1) {
            int hlt = (hlt0 - hlt1) / 2;
            bot.health = bot.health - hlt;
            World.simulation.matrix[xt][yt].health = World.simulation.matrix[xt][yt].health + hlt;
        }
        if (min0 > min1) {
            int min = (min0 - min1) / 2;
            bot.mineral = bot.mineral - min;
            World.simulation.matrix[xt][yt].mineral = World.simulation.matrix[xt][yt].mineral + min;
        }
        return 5;
    }

    //Отдать безвозместно
    public int botGive(Bot bot, int direction, int ra)
    {
        int xt;
        int yt;
        if (ra == 0) {
            xt = xFromVektorR(bot, direction);
            yt = yFromVektorR(bot, direction);
        } else {
            xt = xFromVektorA(bot, direction);
            yt = yFromVektorA(bot, direction);
        }
        if (yt < 0 || yt >= World.simulation.height) {
            return 3;
        } else if (World.simulation.matrix[xt][yt] == null) {
            return 2;
        } else if (World.simulation.matrix[xt][yt].alive <= LV_ORGANIC_SINK) {
            return 4;
        }

        int hlt0 = bot.health;
        int hlt = hlt0 / 4;
        bot.health = hlt0 - hlt;
        World.simulation.matrix[xt][yt].health = World.simulation.matrix[xt][yt].health + hlt;

        int min0 = bot.mineral;
        if (min0 > 3) {
            int min = min0 / 4;
            bot.mineral = min0 - min;
            World.simulation.matrix[xt][yt].mineral = World.simulation.matrix[xt][yt].mineral + min;
            if (World.simulation.matrix[xt][yt].mineral > 999) {
                World.simulation.matrix[xt][yt].mineral = 999;
            }
        }
        return 5;
    }

    //Рождение нового бота делением
    public void botDouble(Bot bot) {
        bot.health = bot.health - 150;
        if (bot.health <= 0) {
            return;
        }

        int n = findEmptyDirection(bot);
        if (n == 8) {
            bot.health = 0;
            return;
        }

        Bot newbot = new Bot();

        int xt = xFromVektorR(bot, n);
        int yt = yFromVektorR(bot, n);

        for (int i = 0; i < MIND_SIZE; i++) {
            newbot.mind[i] = bot.mind[i];
        }
        if (Math.random() < 0.25) {
            int ma = (int) (Math.random() * 64);
            int mc = (int) (Math.random() * 64);
            newbot.mind[ma] = mc;
        }

        newbot.adr = 0;
        newbot.x = xt;
        newbot.y = yt;

        newbot.health = bot.health / 2;
        bot.health = bot.health / 2;
        newbot.mineral = bot.mineral / 2;
        bot.mineral = bot.mineral / 2;

        newbot.alive = 3;

        newbot.c_red = bot.c_red;
        newbot.c_green = bot.c_green;
        newbot.c_blue = bot.c_blue;

        newbot.direction = (int) (Math.random() * 8);

        World.simulation.matrix[xt][yt] = newbot;
    }

    //Рождение новой клетки многоклеточного
    private void botMulti(Bot bot) {
        Bot pbot = bot.mprev;
        Bot nbot = bot.mnext;

        if ((pbot != null) && (nbot != null)) {
            return;
        }

        bot.health = bot.health - 150;
        if (bot.health <= 0) {
            return;
        }
        int n = findEmptyDirection(bot);

        if (n == 8) {
            bot.health = 0;
            return;
        }
        Bot newbot = new Bot();

        int xt = xFromVektorR(bot, n);
        int yt = yFromVektorR(bot, n);

        for (int i = 0; i < MIND_SIZE; i++) {
            newbot.mind[i] = newbot.mind[i];
        }
        if (Math.random() < 0.25) {
            int ma = (int) (Math.random() * 64);
            int mc = (int) (Math.random() * 64);
            newbot.mind[ma] = mc;
        }

        newbot.adr = 0;
        newbot.x = xt;
        newbot.y = yt;

        newbot.health = bot.health / 2;
        bot.health = bot.health / 2;
        newbot.mineral = bot.mineral / 2;
        bot.mineral = bot.mineral / 2;

        newbot.alive = 3;

        newbot.c_red = bot.c_red;
        newbot.c_green = bot.c_green;
        newbot.c_blue = bot.c_blue;

        newbot.direction = (int) (Math.random() * 8);
        World.simulation.matrix[xt][yt] = newbot;

        if (nbot == null) {
            bot.mnext = newbot;
            newbot.mprev = bot;
            newbot.mnext = null;
        } else {
            bot.mprev = newbot;
            newbot.mnext = bot;
            newbot.mprev = null;
        }
    }

    //Копится ли энергия
    public int isHealthGrow(Bot bot) {
        int t;
        if (bot.mineral < 100) {
            t = 0;
        } else {
            if (bot.mineral < 400) {
                t = 1;
            } else {
                t = 2;
            }
        }
        int hlt = 10 - (15 * bot.y / World.simulation.height) + t;
        if (hlt >= 3) {
            return 1;
        } else {
            return 2;
        }
    }

    //Родственники ли боты?
    public int isRelative(Bot bot0, Bot bot1) {
        if (bot1.alive != LV_ALIVE) {
            return 0;
        }
        int dif = 0;
        for (int i = 0; i < MIND_SIZE; i++) {
            if (bot0.mind[i] != bot1.mind[i]) {
                dif = dif + 1;
                if (dif == 2) {
                    return 0;
                }
            }
        }
        return 1;
    }

    public void goGreen(Bot bot, int num) {
        bot.c_green = bot.c_green + num;
        if (bot.c_green + num > 255) {
            bot.c_green = 255;
        }
        int nm = num / 2;

        bot.c_red = bot.c_red - nm;
        if (bot.c_red < 0) {
            bot.c_blue = bot.c_blue +  bot.c_red;
        }
        bot.c_blue = bot.c_blue - nm;
        if (bot.c_blue < 0 ) {
            bot.c_red = bot.c_red + bot.c_blue;
        }
        if (bot.c_red < 0) {
            bot.c_red = 0;
        }
        if (bot.c_blue < 0) {
            bot.c_blue = 0;
        }
    }

    public void goBlue(Bot bot, int num) {
        bot.c_blue = bot.c_blue + num;
        if (bot.c_blue > 255) {
            bot.c_blue = 255;
        }
        int nm = num / 2;

        bot.c_green = bot.c_green - nm;
        if (bot.c_green < 0 ) {
            bot.c_red = bot.c_red + bot.c_green;
        }
        bot.c_red = bot.c_red - nm;
        if (bot.c_red < 0) {
            bot.c_green = bot.c_green +  bot.c_red;
        }
        if (bot.c_red < 0) {
            bot.c_red = 0;
        }
        if (bot.c_green < 0) {
            bot.c_green = 0;
        }
    }

    public void goRed(Bot bot, int num) {
        bot.c_red = bot.c_red + num;
        if (bot.c_red > 255) {
            bot.c_red = 255;
        }
        int nm = num / 2;

        bot.c_green = bot.c_green - nm;
        if (bot.c_green < 0 ) {
            bot.c_blue = bot.c_blue + bot.c_green;
        }

        bot.c_blue = bot.c_blue - nm;
        if (bot.c_blue < 0) {
            bot.c_green = bot.c_green +  bot.c_blue;
        }
        if (bot.c_blue < 0) {
            bot.c_blue = 0;
        }
        if (bot.c_green < 0) {
            bot.c_green = 0;
        }
    }

}
