How to add entities:
1.) Put them in a spritesheet (preferably an existing one)
	1a) If you used a new spritesheet, makesure to preload it at the beginning of game.js
2.) Add it to EntityType.java (give it a new type  number)
	2a) Optionally add it to STATIC_ENTITIES if it won't move
3.) In game.js, create the sprite (ctrl+f for "createSprite" and add it to the end)

That's it, you should be able to add it from debug mode now
(ESC = debug mode, 6 = toggle between tiles & sprites, 1+2 select, 3 = create)