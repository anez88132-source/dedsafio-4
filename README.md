# Desafío 4 – Mod Forge 1.20.1

## Obtener el .jar con GitHub (sin instalar nada)
1. Crea cuenta en https://github.com y un repositorio nuevo (New repository).
2. "uploading an existing file": arrastra TODO el contenido de esta carpeta
   (build.gradle, settings.gradle, gradle.properties, src, .github) y pulsa Commit.
3. Pestaña "Actions" -> espera a que termine "Construir mod" (palomita verde, ~5 min).
4. Entra a esa ejecución -> abajo en "Artifacts" descarga "desafio4-jar".
5. Descomprime ese zip: dentro está desafio4-1.0.0.jar (ese es el mod).

## Uso
- Pon el .jar en la carpeta mods (servidor y jugadores), Forge 1.20.1.
- Tecla J: abre el panel.
- Config: config/desafio4/desafio4.json
- Comandos OP: /desafio4 reload | reset <jugador> | complete <jugador> <id>
