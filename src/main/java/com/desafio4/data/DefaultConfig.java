package com.desafio4.data;

public class DefaultConfig {
    public static final String JSON = """
{
  "title": "Desafío 4",
  "quests": [
    {
      "id": "hierro",
      "title": "Fiebre del hierro",
      "description": "Reúne lingotes de hierro para empezar tu equipo.",
      "type": "collect",
      "target": "minecraft:iron_ingot",
      "amount": 16,
      "rewards": [ { "item": "minecraft:bread", "count": 16 } ]
    },
    {
      "id": "zombies",
      "title": "Cazador de zombis",
      "description": "Elimina zombis que merodean por el mundo.",
      "type": "kill",
      "target": "minecraft:zombie",
      "amount": 10,
      "rewards": [ { "item": "minecraft:golden_apple", "count": 2 } ]
    },
    {
      "id": "diamantes",
      "title": "Minero profundo",
      "description": "Mina minerales de diamante.",
      "type": "mine",
      "target": "minecraft:diamond_ore",
      "amount": 3,
      "rewards": [ { "item": "minecraft:diamond", "count": 3 } ]
    },
    {
      "id": "pico",
      "title": "Herramientas",
      "description": "Fabrica un pico de diamante.",
      "type": "craft",
      "target": "minecraft:diamond_pickaxe",
      "amount": 1,
      "rewards": [ { "item": "minecraft:experience_bottle", "count": 8 } ]
    },
    {
      "id": "nether",
      "title": "Al infierno",
      "description": "Entra por primera vez al Nether.",
      "type": "dimension",
      "target": "minecraft:the_nether",
      "amount": 1,
      "rewards": [ { "item": "minecraft:obsidian", "count": 10 } ]
    },
    {
      "id": "end",
      "title": "El fin",
      "description": "Llega a la dimensión del End.",
      "type": "dimension",
      "target": "minecraft:the_end",
      "amount": 1,
      "rewards": [ { "item": "minecraft:elytra", "count": 1 } ]
    }
  ],
  "objects": [
    { "item": "minecraft:ender_pearl", "note": "Imprescindible para llegar al End." },
    { "item": "minecraft:blaze_rod", "note": "Se consigue en las fortalezas del Nether." },
    { "item": "minecraft:totem_of_undying", "note": "Te salva de una muerte." }
  ],
  "dimensions": [
    { "id": "minecraft:overworld", "name": "Mundo principal", "description": "Tu punto de partida. Recursos, aldeas y cuevas." },
    { "id": "minecraft:the_nether", "name": "Nether", "description": "Dimensión de fuego. Hay fortalezas y bastiones." },
    { "id": "minecraft:the_end", "name": "El End", "description": "La dimensión final y su dragón." }
  ]
}
""";
}
