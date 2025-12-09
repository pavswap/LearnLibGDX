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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends ApplicationAdapter {

    OrthographicCamera camera;
    Sprite sprite;
    Sprite dealer;
    SpriteBatch batch;
    BitmapFont font;
    Random random;

    List<Integer> playerCards;
    List<Integer> dealerCards;

    boolean gameOver = false;
    boolean playerTurn = true;
    String result = "";

    enum Difficulty {
        EASY, MEDIUM, HARD
    }

    Difficulty difficulty = null;
    boolean choosingDifficulty = true;
    float bias = 0f;

    @Override
    public void create() {
        camera = new OrthographicCamera(640, 480);
        batch = new SpriteBatch();
        sprite = new Sprite(new Texture("gamegame_.jpg"));
        sprite.setPosition(-320, -300);
        sprite.setSize(740, 550);

        dealer = new Sprite(new Texture("Dealer.png"));
        dealer.setPosition(30, -270);
        dealer.setSize(300, 300);

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        random = new Random();
        playerCards = new ArrayList<>();
        dealerCards = new ArrayList<>();
    }

    private void dealInitialCards() {
        playerCards.add(randomCard(playerCards));
        dealerCards.add(randomCard(dealerCards));
        playerCards.add(randomCard(playerCards));
        dealerCards.add(randomCard(dealerCards));
    }

    private int randomCard(List<Integer> cards) {
        int newCard;
        do {
            newCard = random.nextInt(10) + 1;
        } while (!cards.isEmpty() && newCard == cards.get(cards.size() - 1));
        return newCard;
    }

    private int calcSum(List<Integer> cards) {
        int sum = 0;
        for (int c : cards) sum += c;
        return sum;
    }

    private void playerHit() {
        if (!gameOver && playerTurn) {
            playerCards.add(randomCard(playerCards));
            if (calcSum(playerCards) > 21) {
                gameOver = true;
                result = "Player Busted! Dealer Wins!";
            }
        }
    }

    private void dealerPlay() {
        playerTurn = false;

        while (calcSum(dealerCards) < calcSum(playerCards) && calcSum(dealerCards) <= 21) {
            int dealerSum = calcSum(dealerCards);
            int needed = 21 - dealerSum;
            int card;

            if (random.nextFloat() < bias) {
                if (needed >= 1 && needed <= 10) {
                    card = needed;
                    if (!dealerCards.isEmpty() &&
                        card == dealerCards.get(dealerCards.size() - 1)) {
                        card = (card == 10 ? 9 : card + 1);
                    }
                } else {
                    do {
                        card = random.nextInt(4) + 7;
                    } while (card == dealerCards.get(dealerCards.size() - 1));
                }
            } else {
                card = randomCard(dealerCards);
            }

            dealerCards.add(card);

            if (calcSum(dealerCards) > 21) break;
        }

        checkWinner();
    }

    private void checkWinner() {
        int p = calcSum(playerCards);
        int d = calcSum(dealerCards);

        if (d > 21) result = "Dealer Busted! Player Wins!";
        else if (p > d) result = "Player Wins!";
        else if (d > p) result = "Dealer Wins!";
        else result = "Dealer Wins! HAHA!";

        gameOver = true;
    }

    private void startGame() {
        choosingDifficulty = false;
        dealInitialCards();
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.DARK_GRAY);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        sprite.draw(batch);
        dealer.draw(batch);

        font.draw(batch, "BLACKJACK", -300, 200);

        if (choosingDifficulty) {
            font.draw(batch, "SELECT DIFFICULTY", -300, 100);
            font.draw(batch, "1 - EASY", -300, 50);
            font.draw(batch, "2 - MEDIUM", -300, 20);
            font.draw(batch, "3 - HARD", -300, -10);
            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                bias = 0.15f; difficulty = Difficulty.EASY; startGame();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                bias = 0.40f; difficulty = Difficulty.MEDIUM; startGame();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                bias = 0.60f; difficulty = Difficulty.HARD; startGame();
            }
            return;
        }

        if (!gameOver) {
            font.draw(batch, "Press SPACE to HIT", -300, 150);
            font.draw(batch, "Press ENTER to STAY", -300, 120);
        }

        font.draw(batch, "Player Cards: " + playerCards + " = " + calcSum(playerCards), -300, 50);

        if (!gameOver) {
            font.draw(batch, "Dealer Cards: [" + dealerCards.get(0) + ", ?]", -300, 0);
        } else {
            font.draw(batch, "Dealer Cards: " + dealerCards + " = " + calcSum(dealerCards), -300, 0);
            font.draw(batch, result, -300, -50);
            font.draw(batch, "Press R to Restart", -300, -80);
        }

        batch.end();

        if (!gameOver && playerTurn) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) playerHit();
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) dealerPlay();
        }

        if (gameOver && Gdx.input.isKeyJustPressed(Input.Keys.R)) restartGame();
    }

    private void restartGame() {
        playerCards.clear();
        dealerCards.clear();
        gameOver = false;
        playerTurn = true;
        result = "";
        choosingDifficulty = true;
        bias = 0f;
        difficulty = null;
    }

    @Override
    public void dispose() {
        batch.dispose();
        sprite.getTexture().dispose();
        font.dispose();
    }
}
