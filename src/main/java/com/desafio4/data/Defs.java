package com.desafio4.data;

import java.util.ArrayList;
import java.util.List;

/** Estructura del archivo config/desafio4/desafio4.json */
public class Defs {
    public String title = "Desafío 4";
    public List<Quest> quests = new ArrayList<>();
    public List<Obj> objects = new ArrayList<>();
    public List<Dim> dimensions = new ArrayList<>();

    public static class Quest {
        public String id;
        public String title = "";
        public String description = "";
        /** collect | kill | mine | craft | dimension */
        public String type;
        public String target;
        public int amount = 1;
        public List<Reward> rewards = new ArrayList<>();
    }

    public static class Reward {
        public String item;
        public int count = 1;
    }

    public static class Obj {
        public String item;
        public String note = "";
    }

    public static class Dim {
        public String id;
        public String name = "";
        public String description = "";
    }
}
