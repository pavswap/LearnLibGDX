package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;


public class Main extends ApplicationAdapter {

    OrthographicCamera camera; // Camera is used to View the screen
    Sprite sprite; // Sprite is the object which stores the Image
    SpriteBatch batch; // SpriteBatch is used to manage Sprites

    @Override
    public void create() {

        camera = new OrthographicCamera(640, 480);
        batch = new SpriteBatch();
        sprite = new Sprite(new Texture("game1.jpg")); // Initalizing Sprite to the Image game1.jpg
        sprite.setPosition(-320, -300); // Setting Image Position in the screen, the center of the screen is (0, 0)
        sprite.setSize(640, 480); // Resizing the Sprite to fit the screen
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        ScreenUtils.clear(Color.BLACK);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        sprite.draw(batch); // this Method will Draw the Sprite on the Screen
        batch.end();
    }

    @Override
    public void dispose() {

    }
}
