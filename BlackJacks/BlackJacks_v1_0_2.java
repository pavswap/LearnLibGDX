package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.audio.Music;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackJacks_v1_0_2 extends ApplicationAdapter {

    // ===== CAMERA & RENDERING =====
    OrthographicCamera camera;
    SpriteBatch batch;
    Sprite background;
    Sprite dealerSprite;
    Sprite title;
    BitmapFont font;
    Music backgroundMusic;

    final float CARD_WIDTH = 60;
    final float CARD_HEIGHT = 90;


    // ===== GAME LOGIC =====
    Random random;
    List<Card> playerCards;
    List<Card> dealerCards;
    List<Card> deck;

    boolean gameOver = false;
    boolean playerTurn = true;
    String result = "";

    // ===== DIFFICULTY =====
    enum Difficulty {
        EASY, MEDIUM, HARD
    }

    Difficulty difficulty = Difficulty.EASY;
    float bias = 0f;

    // ===== GAME STATE =====
    enum GameState {
        MAIN_MENU,
        DIFFICULTY_MENU,
        HOW_TO_PLAY,
        CREDITS,
        GAME
    }

    GameState currentState = GameState.MAIN_MENU;

    // ===== MENU POSITIONS =====
    float menuX = -300;

    @Override
    public void create() {
        camera = new OrthographicCamera(640, 480);
        batch = new SpriteBatch();

        background = new Sprite(new Texture("gamegame_.jpg"));
        background.setSize(740, 550);
        background.setPosition(-320, -300);


        title = new Sprite(new Texture(("title-removebg-preview.png")));
        title.setSize(500, 200);
        title.setPosition(-310, 50);

        dealerSprite = new Sprite(new Texture("Dealer.png"));
        dealerSprite.setSize(300, 370);
        dealerSprite.setPosition(30, -300);

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        random = new Random();
        playerCards = new ArrayList<>();
        dealerCards = new ArrayList<>();

        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Music.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f); // 50% volume
        backgroundMusic.play();

    }

    // ================= CARD LOGIC =================

    private void dealInitialCards() {
        playerCards.add(drawCard());
        dealerCards.add(drawCard());
        playerCards.add(drawCard());
        dealerCards.add(drawCard());
    }

    private void playerHit() {
        playerCards.add(drawCard());
        if (calcSum(playerCards) > 21) {
            result = "Player Busted! Dealer Wins!";
            gameOver = true;
        }
    }


    private void dealerPlay() {
        playerTurn = false;

        while (calcSum(dealerCards) < 17 && dealerCards.size() < 6) {
            dealerCards.add(drawCard());
        }

        checkWinner();
    }

    private int calcSum(List<Card> cards) {
        int sum = 0;
        for (Card c : cards) sum += c.value;
        return sum;
    }

    private void checkWinner() {
        int p = calcSum(playerCards);
        int d = calcSum(dealerCards);

        if (d > 21) result = "Dealer Busted! Player Wins!";
        else if (p > d) result = "Player Wins!";
        else result = "Dealer Wins!";

        gameOver = true;
    }

    private void startGame() {
        createDeck();
        playerCards.clear();
        dealerCards.clear();
        gameOver = false;
        playerTurn = true;
        result = "";
        dealInitialCards();
        currentState = GameState.GAME;
    }

    private void drawCardHand(List<Card> cards, float startX, float startY) {
        int cardsPerRow = 10;
        float spacingX = 65;
        float spacingY = 100;

        for (int i = 0; i < cards.size(); i++) {
            int row = i / cardsPerRow;
            int col = i % cardsPerRow;

            Texture tex = new Texture(Gdx.files.internal(cards.get(i).texturePath));
            batch.draw(
                tex,
                startX + col * spacingX,
                startY - row * spacingY,
                CARD_WIDTH,
                CARD_HEIGHT
            );
        }
    }


    // ================= RENDER =================

    @Override
    public void render() {
        ScreenUtils.clear(Color.DARK_GRAY);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        background.draw(batch);




        //font.draw(batch, "BLACKJACKS", -300, 200);

        switch (currentState) {
            case MAIN_MENU:
                dealerSprite.draw(batch);
                drawMainMenu();
                break;
            case DIFFICULTY_MENU:
                dealerSprite.draw(batch);
                drawDifficultyMenu();
                break;
            case HOW_TO_PLAY:
                drawHowToPlay();
                break;
            case CREDITS:
                dealerSprite.draw(batch);
                drawCredits();
                break;
            case GAME:
                dealerSprite.draw(batch);
                drawGame();
                break;
        }

        batch.end();
    }

    // ================= SCREENS =================

    private void drawMainMenu() {

        title.draw(batch);
        //font.draw(batch, "MAIN MENU", menuX, 160);
        font.draw(batch, "1- PLAY GAME", menuX, 60);
        font.draw(batch, "2- CHOOSE DIFFICULTY", menuX, 20);
        font.draw(batch, "3- HOW TO PLAY", menuX, -20);
        font.draw(batch, "4- CREDITS", menuX, -60);

        // Player Helper

        // Save old scale
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;

        // Save old color
        Color oldColor = new Color(font.getColor());

        // Apply smaller size
        font.getData().setScale(1.2f);
        // Apply transparency (0 = invisible, 1 = fully visible)
        font.setColor(1f, 1f, 1f, 0.6f);   // 60% opacity
        font.draw(batch, "Press 1,2,3,4 to Navigate\nin the Menu", -310, -220);
        // Restore scale
        font.getData().setScale(oldScaleX, oldScaleY);
        // Restore color
        font.setColor(oldColor);


        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            difficulty = Difficulty.EASY;
            bias = 0f;
            startGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            currentState = GameState.DIFFICULTY_MENU;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            currentState = GameState.HOW_TO_PLAY;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            currentState = GameState.CREDITS;
        }
    }

    private void drawDifficultyMenu() {
        font.draw(batch, "SELECT DIFFICULTY", menuX, 160);
        font.draw(batch, "1 - EASY", menuX, 100);
        font.draw(batch, "2 - MEDIUM", menuX, 60);
        font.draw(batch, "3 - HARD", menuX, 20);


        // Player Helper

        // Save old scale
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        // Save old color
        Color oldColor = new Color(font.getColor());
        // Apply smaller size
        font.getData().setScale(1.2f);
        // Apply transparency (0 = invisible, 1 = fully visible)
        font.setColor(1f, 1f, 1f, 0.6f);   // 60% opacity
        font.draw(batch, "Press ESC to go back", -310, -220);
        // Restore scale
        font.getData().setScale(oldScaleX, oldScaleY);
        // Restore color
        font.setColor(oldColor);

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            difficulty = Difficulty.EASY;
            bias = 0f;
            currentState = GameState.MAIN_MENU;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            difficulty = Difficulty.MEDIUM;
            bias = 0.3f;
            currentState = GameState.MAIN_MENU;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            difficulty = Difficulty.HARD;
            bias = 0.5f;
            currentState = GameState.MAIN_MENU;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            currentState = GameState.MAIN_MENU;
        }
    }

    private void drawHowToPlay() {
        font.draw(batch, "HOW TO PLAY", -300, 150);
        font.draw(batch, "Press SPACE - HIT", -300, 100);
        font.draw(batch, "Press ENTER - STAY", -300, 60);
        font.draw(batch, "To win, get a score more than Dealer", -300, 20);
        font.draw(batch, "and less than equal to 21", -300, -20);


        // Player Helper

        // Save old scale
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        // Save old color
        Color oldColor = new Color(font.getColor());
        // Apply smaller size
        font.getData().setScale(1.2f);
        // Apply transparency (0 = invisible, 1 = fully visible)
        font.setColor(1f, 1f, 1f, 0.6f);   // 60% opacity
        font.draw(batch, "Press ESC to go back", -310, -220);
        // Restore scale
        font.getData().setScale(oldScaleX, oldScaleY);
        // Restore color
        font.setColor(oldColor);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            currentState = GameState.MAIN_MENU;
        }
    }

    private void drawCredits() {
        font.draw(batch, "CREDITS", -300, 100);
        font.draw(batch, "A  game by Paavan Swaroop\nand Jayadev Kompella", -300, 40);


        // Player Helper

        // Save old scale
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        // Save old color
        Color oldColor = new Color(font.getColor());
        // Apply smaller size
        font.getData().setScale(1.2f);
        // Apply transparency (0 = invisible, 1 = fully visible)
        font.setColor(1f, 1f, 1f, 0.6f);   // 60% opacity
        font.draw(batch, "Press ESC to go back", -310, -220);
        // Restore scale
        font.getData().setScale(oldScaleX, oldScaleY);
        // Restore color
        font.setColor(oldColor);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            currentState = GameState.MAIN_MENU;
        }
    }

    private void drawGame() {

        // Player cards
        drawCardHand(playerCards, -300, -50);

        // Dealer cards
        if (!gameOver) {
            drawCardHand(dealerCards.subList(0,1), -300, 100);
            Texture back = new Texture("cards/Back_Blue.png");
            batch.draw(back, -235, 100, 60, 90);
        } else {
            drawCardHand(dealerCards, -300, 100);
            font.draw(batch, result, -300, -100);


            // Player Helper

            // Save old scale
            float oldScaleX = font.getData().scaleX;
            float oldScaleY = font.getData().scaleY;
            // Save old color
            Color oldColor = new Color(font.getColor());
            // Apply smaller size
            font.getData().setScale(1.2f);
            // Apply transparency (0 = invisible, 1 = fully visible)
            font.setColor(1f, 1f, 1f, 0.6f);   // 60% opacity
            font.draw(batch, "Press R to play again", -310, -200);
            // Restore scale
            font.getData().setScale(oldScaleX, oldScaleY);
            // Restore color
            font.setColor(oldColor);
        }

        font.draw(batch, "Player Score: " + calcSum(playerCards), 60, 220);
        // Player Helper

        // Save old scale
        float oldScaleX = font.getData().scaleX;
        float oldScaleY = font.getData().scaleY;
        // Save old color
        Color oldColor = new Color(font.getColor());
        // Apply smaller size
        font.getData().setScale(1.2f);
        // Apply transparency (0 = invisible, 1 = fully visible)
        font.setColor(1f, 1f, 1f, 0.6f);   // 60% opacity
        font.draw(batch, "Press Space to Hit, ENTER to Stay", -310, -220);
        // Restore scale
        font.getData().setScale(oldScaleX, oldScaleY);
        // Restore color
        font.setColor(oldColor);

        if (!gameOver && playerTurn) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) playerHit();
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) dealerPlay();
        }

        if (gameOver && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            currentState = GameState.MAIN_MENU;
        }
    }


    @Override
    public void dispose() {
        batch.dispose();
        background.getTexture().dispose();
        dealerSprite.getTexture().dispose();
        font.dispose();
    }

    private void createDeck() {
        deck = new ArrayList<>();

        String[] ranks = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        char[] suits = {'C','D','H','S'};

        for (char suit : suits) {
            for (String rank : ranks) {
                deck.add(new Card(rank, suit));
            }
        }

        java.util.Collections.shuffle(deck);
    }

    private Card drawCard() {
        return deck.remove(deck.size() - 1);
    }

}
