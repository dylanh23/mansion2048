package dhare.mansion2048;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.andengine.AndEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.color.Color;

import java.util.Random;

public class GameActivity extends LayoutGameActivity {
    //TODO: move some methods to new class
    //TODO: have to buy: art different sizes
    //TODO: hard: language
    //TODO: more features
    //final
    //tags
    protected static final int CAMERA_WIDTH = 480;
    protected static int CAMERA_HEIGHT = 800;
    protected static final int BORDER_SIZE = 13;
    protected static final int TILE_SIZE = 94;
    protected static final int BUFFER_WIDTH = (CAMERA_WIDTH - (BORDER_SIZE * 5 + TILE_SIZE * 4)) / 2; //19
    protected static int BUFFER_HEIGHT;
    protected static final int globalSpeed = 30;
    Scene scene;
    Random randomGenerater = new Random();
    long createdTime = System.currentTimeMillis();
    int direction = -1;
    int totalScore = 0;
    int addScore;
    float mainXScale;
    Font fnt;
    Text totalText;
    Text bestText;
    Sprite bst;
    Sprite totl;
    Sprite totlRect;
    Sprite bstRect;
    Sprite tryagn;
    Sprite gameovr;
    Rectangle lossRect;
    Boolean govr = false;
    float bstRectSizeX;
    float totlRectSizeX;
    String bestScoreSaved;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;

    private void requestNewInterstitial() {
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        //3:4, 2:3, 10:16, 3:5, and 9:16
        //0.75, 0.66, 0.625, 0.6, 0.5625
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            CAMERA_HEIGHT = (int) (CAMERA_WIDTH / ((float) size.x / (float) size.y));
        } else {
            CAMERA_HEIGHT = (int) (CAMERA_WIDTH / ((float) display.getWidth() / (float) display.getHeight()));
        }
        //CAMERA_HEIGHT = (int) (480 / (10.0 / 16.0));
        //BUFFER_HEIGHT = (CAMERA_HEIGHT - (BORDER_SIZE * 5 + TILE_SIZE * 4)) / 2;
        Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        //EngineOptions options = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), mCamera);
        //options.getTouchOptions().setNeedsMultiTouch(true);
        //return options;
        adRequest = new AdRequest.Builder().addTestDevice("DDB504E461FF179D726AD9B5F625CBE1").build();
        adRequest = new AdRequest.Builder().build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6184270616715379/5736300240");
        requestNewInterstitial();
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        mInterstitialAd.show();
        return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), mCamera);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws
            Exception {
        loadGFX();
        fnt = FontFactory.createFromAsset(this.getFontManager(), this.getTextureManager(), 256, 256, this.getAssets(), "fnt/ClearSans-Bold.ttf", 28, true, android.graphics.Color.WHITE);
        //fnt = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), "fnt/ClearSans-Regular.TTF", 32);
        fnt.load();
        //addScore = ;
        //TODO: add: create test

        //SharedPreferences sp = getPreferences(MODE_PRIVATE);
        //sp.edit().clear().apply();
        //sp.edit().putInt("Score", 25644).apply();
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    //TextureRegion house0;
    TextureRegion house1;
    TextureRegion house2;
    TextureRegion house3;
    TextureRegion house4;
    TextureRegion house5;
    TextureRegion house6;
    TextureRegion house7;
    TextureRegion house8;
    TextureRegion house9;
    TextureRegion house10;
    TextureRegion house11;
    TextureRegion house12;
    TextureRegion title;
    TextureRegion best;
    TextureRegion score;
    TextureRegion roundedRect;
    TextureRegion newGame;
    TextureRegion htp;
    TextureRegion tryagain;
    TextureRegion gameover;

    private void loadGFX() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        //TODO: do they scale all sprites down and then scale whole camera up?
        //if > 1000 then big
        //485 x 485 limit size
        //nearest 256
        //BitmapTextureAtlas b = new BitmapTextureAtlas(this.getTextureManager(), 1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlas b = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        int x = 0;
        house1 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house1.png", x, 0);
        x += 95;
        house2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house2.png", x, 0);
        x += 95;
        house3 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house3.png", x, 0);
        x += 95;
        house4 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house4.png", x, 0);
        x += 95;
        house5 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house5.png", x, 0);
        x += 95;
        house6 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house6.png", x, 0);
        x += 95;
        house7 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house7.png", x, 0);
        x += 95;
        house8 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house8.png", x, 0);
        x += 95; //760
        house9 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house9.png", x, 0);
        x += 95;
        house10 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house10.png", x, 0);
        //x = 855
        x = 0;
        house11 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house11.png", x, 95);
        //house0 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house0.png", 475, 0);
        x += 95;
        title = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "title2.png", x, 95); //369
        x += title.getWidth() + 1;
        best = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "best.png", x, 95); //76
        x += best.getWidth() + 1;
        newGame = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "newGame.png", x, 95); //297
        x += newGame.getWidth() + 1;
        //x = 840
        house12 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "house12.png", x, 95);
        x += newGame.getWidth() + 1;
        x = 0;
        score = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "score.png", x, 95 + 194); //101
        x += score.getWidth() + 1;
        roundedRect = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "roundedRect.png", x, 95 + 194); //101
        x += roundedRect.getWidth() + 1;
        htp = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "htp.png", x, 95 + 194); //754
        //x += htp.getWidth() + 1;
        x = 0;
        gameover = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "gameover.png", x, 95 + 194 + 102);
        x += gameover.getWidth() + 1;
        tryagain = BitmapTextureAtlasTextureRegionFactory.createFromAsset(b, this, "tryagain.png", x, 95 + 194 + 102);
        x += tryagain.getWidth() + 1;
        b.load();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        scene = new Scene();
        scene.setBackground(new Background(new Color((float) 250 / 250, (float) 248 / 250, (float) 239 / 250)));
        scene.setBackgroundEnabled(true);
        scene.registerUpdateHandler(new IUpdateHandler() {
            public void reset() {
            }

            public void onUpdate(float pSecondsElapsed) {
                update();
            }
        });
        pOnCreateSceneCallback.onCreateSceneFinished(scene);
    }

    @Override
    public void onPopulateScene(final Scene pScene, OnPopulateSceneCallback
            pOnPopulateSceneCallback) throws Exception {
        mainXScale = (title.getWidth() / 782) * CAMERA_WIDTH / title.getWidth();
        float mainYScale = (title.getHeight() / (782 / title.getWidth() * CAMERA_HEIGHT)) * CAMERA_HEIGHT / title.getHeight();
        //(CAMERA_WIDTH - 51 - 150) / title.getWidth();
        //size
        Sprite ttl = new Sprite(BUFFER_WIDTH, BUFFER_WIDTH, title, mEngine.getVertexBufferObjectManager()) {
        };
        ttl.setScaleCenter(0f, 0f);
        ttl.setScale(mainXScale);
        scene.attachChild(ttl);
        BUFFER_HEIGHT = (int) (ttl.getY() + ttl.getHeightScaled()) + BUFFER_WIDTH;
        float htscale = (TILE_SIZE * 4 + BORDER_SIZE * 5) / htp.getWidth();
        float gap = (CAMERA_HEIGHT - (BUFFER_HEIGHT + TILE_SIZE * 4 + BORDER_SIZE * 5) - 40 - (htp.getHeight() * htscale)) / 2.5f;
        if (gap < 3) {
            gap = (CAMERA_HEIGHT - (BUFFER_HEIGHT + TILE_SIZE * 4 + BORDER_SIZE * 5) - 40 - (htp.getHeight() * htscale)) / 1.5f;
            if (gap > 3) {
                Sprite howtp = new Sprite(BUFFER_WIDTH, (BUFFER_HEIGHT + TILE_SIZE * 4 + BORDER_SIZE * 5) + gap, htp, mEngine.getVertexBufferObjectManager()) {
                };
                howtp.setScaleCenter(0f, 0f);
                howtp.setScale(htscale); //455
                scene.attachChild(howtp);
            }
        } else {
            Sprite howtp = new Sprite(BUFFER_WIDTH, (BUFFER_HEIGHT + TILE_SIZE * 4 + BORDER_SIZE * 5) + gap, htp, mEngine.getVertexBufferObjectManager()) {
            };
            howtp.setScaleCenter(0f, 0f);
            howtp.setScale(htscale); //455
            scene.attachChild(howtp);
            float h = howtp.getY() + howtp.getHeightScaled() + gap;
            Line l = new Line(BUFFER_WIDTH, h, CAMERA_WIDTH - BUFFER_WIDTH, h, this.getVertexBufferObjectManager());
            l.setLineWidth(1);
            l.setColor(new Color((float) 216 / 255, (float) 212 / 255, (float) 208 / 255));
            scene.attachChild(l);
        }

        Sprite nGame = new Sprite(CAMERA_WIDTH - BUFFER_WIDTH - (newGame.getWidth() * mainXScale), ttl.getY() + ttl.getHeightScaled() - (newGame.getHeight() * mainXScale), newGame, this.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
                        break;
                    case TouchEvent.ACTION_UP:
                        if (!govr) {
                            newTiles();
                        }
                        break;
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        nGame.setScaleCenter(0, 0);
        nGame.setScale(mainXScale);
        scene.attachChild(nGame);
        scene.registerTouchArea(nGame);
        /*float totlRectScaleX = (84f / 778) * CAMERA_WIDTH / roundedRect.getWidth();
        float totlRectScaleY = (78f / 1291) * CAMERA_HEIGHT / roundedRect.getHeight();
        Sprite totlRect = new Sprite(CAMERA_WIDTH - BUFFER_WIDTH - roundedRect.getWidth() * totlRectScaleX, (BUFFER_HEIGHT - title.getHeight() * mainXScale) / 2, roundedRect, mEngine.getVertexBufferObjectManager()) {
        };
        totlRect.setScaleCenter(0f, 0f);
        totlRect.setScale(totlRectScaleX, mainYScale);*/
        bstRectSizeX = 145f * mainXScale;
        float rectSizeY = roundedRect.getHeight() * mainXScale;
        bstRect = new Sprite(CAMERA_WIDTH - BUFFER_WIDTH - bstRectSizeX, BUFFER_WIDTH, roundedRect, mEngine.getVertexBufferObjectManager()) {
        };
        bstRect.setSize(bstRectSizeX, rectSizeY);
        scene.attachChild(bstRect);
        bst = new Sprite(CAMERA_WIDTH - BUFFER_WIDTH - bstRectSizeX + (bstRectSizeX / 2 - best.getWidth() * mainXScale / 2), BUFFER_WIDTH + (10f / 1040) * CAMERA_HEIGHT, best, mEngine.getVertexBufferObjectManager()) {
        };
        //Sprite totl = new Sprite(250, 50, score, mEngine.getVertexBufferObjectManager()) {};
        bst.setScaleCenter(0, 0);
        bst.setScale(mainXScale);
        scene.attachChild(bst);
        bestText = new Text(BUFFER_WIDTH, BUFFER_WIDTH + 20, fnt, "New High: 123456789", this.getVertexBufferObjectManager());
        SharedPreferences bestScore = getPreferences(MODE_PRIVATE);
        bestScoreSaved = String.valueOf(bestScore.getInt("Score", 0));
        if (bestScoreSaved.length() > 3) {
            bstRectSizeX += 8 * (bestScoreSaved.length() - 3);
            bstRect.setSize(bstRectSizeX, rectSizeY);
            bstRect.setX(CAMERA_WIDTH - BUFFER_WIDTH - bstRectSizeX);
            bst.setX(CAMERA_WIDTH - BUFFER_WIDTH - bstRectSizeX / 2 - bst.getWidthScaled() / 2);
        }
        bestText.setText(String.valueOf(bestScore.getInt("Score", 0)));
        bestText.setX(CAMERA_WIDTH - BUFFER_WIDTH - bstRectSizeX / 2 - bestText.getWidth() / 2 - 2);
        scene.attachChild(bestText);
        //float totlRectSizeX = (120f / 782) * CAMERA_WIDTH;
        //float totlRectSizeY = (roundedRect.getHeight() / (782 / title.getWidth() * CAMERA_HEIGHT)) * CAMERA_HEIGHT;
        totlRectSizeX = 145f * mainXScale;
        totlRect = new Sprite(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - totlRectSizeX - bstRectSizeX, BUFFER_WIDTH, roundedRect, mEngine.getVertexBufferObjectManager()) {
        };
        totlRect.setSize(totlRectSizeX, rectSizeY);
        scene.attachChild(totlRect);
        /*Sprite totl = new Sprite(CAMERA_WIDTH - BUFFER_WIDTH - totlRectSizeX + (totlRectSizeX / 2 - score.getWidth() * mainXScale / 2), (BUFFER_HEIGHT - title.getHeight() * mainXScale) / 2 + (10f / 1040) * CAMERA_HEIGHT, score, mEngine.getVertexBufferObjectManager()) {
        };
        totl.setScaleCenter(0f, 0f);
        totl.setScale(mainXScale);*/
        totl = new Sprite(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - bstRectSizeX - totlRectSizeX / 2 - score.getWidth() * mainXScale / 2, BUFFER_WIDTH + (10f / 1040) * CAMERA_HEIGHT, score, mEngine.getVertexBufferObjectManager()) {
        };
        //Sprite totl = new Sprite(250, 50, score, mEngine.getVertexBufferObjectManager()) {};
        totl.setScaleCenter(0, 0);
        totl.setScale(mainXScale);
        scene.attachChild(totl);
        totalText = new Text(BUFFER_WIDTH, BUFFER_WIDTH + 20, fnt, "New High: 123456789", this.getVertexBufferObjectManager());
        totalText.setText(String.valueOf(totalScore));
        totalText.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - bstRectSizeX - totlRectSizeX / 2 - totalText.getWidth() / 2);
        scene.attachChild(totalText);
        Rectangle rect = new Rectangle(BUFFER_WIDTH, BUFFER_HEIGHT, 441, 441, this.getVertexBufferObjectManager());
        rect.setZIndex(-2);
        rect.setColor((float) 187 / 255, (float) 173 / 255, (float) 160 / 255);
        scene.attachChild(rect);

        for (int x = 0; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                tiles[x][y] = new Tile();
                tiles[x][y].house = new Sprite(x * (BORDER_SIZE + TILE_SIZE) + BORDER_SIZE + BUFFER_WIDTH, y * (BORDER_SIZE + TILE_SIZE) + BORDER_SIZE + BUFFER_HEIGHT, house1, mEngine.getVertexBufferObjectManager()) {
                };
                scene.attachChild(tiles[x][y].house);
                /*
                h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house0, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house0);
                 */
                Rectangle r = new Rectangle(x * (BORDER_SIZE + TILE_SIZE) + BORDER_SIZE + BUFFER_WIDTH, y * (BORDER_SIZE + TILE_SIZE) + BORDER_SIZE + BUFFER_HEIGHT, TILE_SIZE, TILE_SIZE, this.getVertexBufferObjectManager());
                r.setColor((float) 205 / 255, (float) 192 / 255, (float) 180 / 255);
                r.setZIndex(-1);
                pScene.attachChild(r);
            }
        }

        for (int i = 0; i < 1; i++) { //1
            spawnNew();
        }

        lossRect = new Rectangle(BUFFER_WIDTH, BUFFER_HEIGHT, BORDER_SIZE * 5 + TILE_SIZE * 4, BORDER_SIZE * 5 + TILE_SIZE * 4, this.getVertexBufferObjectManager());
        lossRect.setColor((float) 238 / 255, (float) 228 / 255, (float) 218 / 255, 0f);
        lossRect.setVisible(false);
        lossRect.setZIndex(2);
        scene.attachChild(lossRect);
        tryagn = new Sprite((CAMERA_WIDTH / 2) - (tryagain.getWidth() * mainXScale / 2), (BUFFER_HEIGHT + TILE_SIZE * 2 + BORDER_SIZE * 3) - ((tryagain.getHeight() * mainXScale) / 2) - (BORDER_SIZE / 2) + (BUFFER_WIDTH * 2), tryagain, mEngine.getVertexBufferObjectManager()) {
            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_DOWN:
                        break;
                    case TouchEvent.ACTION_UP:
                        newTiles();
                        tryagn.setVisible(false);
                        scene.unregisterTouchArea(tryagn);
                        lossRect.setVisible(false);
                        lossRect.setAlpha(0f);
                        gameovr.setVisible(false);
                        govr = false;
                        break;
                }
                return super.
                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        tryagn.setScaleCenter(0, 0);
        tryagn.setScale(mainXScale);
        tryagn.setZIndex(3);
        tryagn.setVisible(false);
        scene.attachChild(tryagn);
        gameovr = new Sprite((CAMERA_WIDTH / 2) - (gameover.getWidth() * mainXScale / 2), (BUFFER_HEIGHT + TILE_SIZE * 2 + BORDER_SIZE * 3) - ((gameover.getHeight() * mainXScale) / 2) - (BORDER_SIZE / 2) - (BUFFER_WIDTH * 2), gameover, mEngine.getVertexBufferObjectManager()) {
        };
        gameovr.setScaleCenter(0, 0);
        gameovr.setScale(mainXScale);
        gameovr.setZIndex(3);
        gameovr.setVisible(false);
        scene.attachChild(gameovr);

        changeStage();
        createTouchArea(pScene);
        /*AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(adRequest);*/
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }


    void newTiles() {
        //TODO: add: on destroy add try
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        prefs.edit().putInt("Tries", prefs.getInt("Tries", 0) + 1).apply();
        if (prefs.getInt("Tries", 0) > 2) {
            prefs.edit().putInt("Tries", 0).apply();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }
            });
        }
        totalScore = 0;
        totalText.setText(String.valueOf(totalScore));
        totlRectSizeX = 145f * mainXScale;
        //totalText.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE - bstRect.getWidthScaled() - totlRect.getWidthScaled() / 2 - totalText.getWidth() / 2);
        totalText.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - bstRectSizeX - totlRectSizeX / 2 - totalText.getWidth() / 2);
        for (int x = 0; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                tiles[x][y].stage = 0;
            }
        }
        for (int i = 0; i < 1; i++) {
            spawnNew();
        }
        changeStage();
        totlRect.setSize(totlRectSizeX, totlRect.getHeightScaled());
        totlRect.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - totlRectSizeX - bstRectSizeX);
        totl.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - bstRectSizeX - totlRectSizeX / 2 - totl.getWidthScaled() / 2);
    }

    private void createTouchArea(final Scene pScene) {
        Rectangle r = new Rectangle(BUFFER_WIDTH, BUFFER_HEIGHT, BORDER_SIZE * 5 + TILE_SIZE * 4, BORDER_SIZE * 5 + TILE_SIZE * 4 + TILE_SIZE * 2, this.getVertexBufferObjectManager()) {
            float x = -1;
            float y;

            @Override
            public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
                                         final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                switch (pAreaTouchEvent.getAction()) {
                    case TouchEvent.ACTION_MOVE:
                        if (x == -1) {
                            x = pTouchAreaLocalX;
                            y = pTouchAreaLocalY;
                        }
                        return true;
                    case TouchEvent.ACTION_UP:
                        //if (direction == -1) {
                        if (x != -1) {
                            if (Math.abs(x - pTouchAreaLocalX) > Math.abs(y - pTouchAreaLocalY)) {
                                if (x > pTouchAreaLocalX) {
                                    for (int y = 0; y < 4; y++) {
                                        for (int x = 0; x < 3; x++) {
                                            left(x, y);
                                        }
                                    }
                                    if (move) {
                                        direction = 3;
                                        //move = false;
                                    }
                                } else {
                                    for (int y = 0; y < 4; y++) {
                                        for (int x = 3; x > 0; x--) {
                                            right(x, y);
                                        }
                                    }
                                    if (move) {
                                        direction = 1;
                                        //move = false;
                                    }
                                }
                            } else if (Math.abs(y - pTouchAreaLocalY) > Math.abs(x - pTouchAreaLocalX)) {
                                if (y < pTouchAreaLocalY) {
                                    for (int x = 0; x < 4; x++) {
                                        for (int y = 3; y > 0; y--) {
                                            down(x, y);
                                        }
                                    }
                                    if (move) {
                                        direction = 2;
                                        //move = false;
                                    }
                                } else {
                                    for (int x = 0; x < 4; x++) {
                                        for (int y = 0; y < 3; y++) {
                                            up(x, y);
                                        }
                                    }
                                    if (move) {
                                        direction = 0;
                                        //move = false;
                                    }
                                }
                            }
                            x = -1;
                            y = -1;
                            return true;
                        }
                }
                //}

                return super.

                        onAreaTouched(pAreaTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        };
        r.setColor(0, 0, 0, 0);
        //r.setZIndex(100);
        pScene.attachChild(r);
        pScene.registerTouchArea(r);
    }

    boolean move;
    long moveTime;

    void update() {
        if (direction != -1) {
            moveTiles();
        }
    }

    private void moveTiles() {
        //if (System.currentTimeMillis() - createdTime >= moveTime) {
        move = false;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (tiles[x][y].distance != 0) {
                    move = true;
                    if (tiles[x][y].distance < tiles[x][y].speed) {
                        tiles[x][y].speed = tiles[x][y].distance;
                    }
                    tiles[x][y].distance -= tiles[x][y].speed;
                    switch (direction) {
                        case 0:
                            tiles[x][y].house.setY(tiles[x][y].house.getY() - tiles[x][y].speed);
                            break;
                        case 1:
                            tiles[x][y].house.setX(tiles[x][y].house.getX() + tiles[x][y].speed);
                            break;
                        case 2:
                            tiles[x][y].house.setY(tiles[x][y].house.getY() + tiles[x][y].speed);
                            break;
                        case 3:
                            tiles[x][y].house.setX(tiles[x][y].house.getX() - tiles[x][y].speed);
                            break;
                    }
                }
            }
        }
        //moveTime = System.currentTimeMillis() - createdTime + 1;
        if (!move) {
            direction = -1;
            changeStage();
        }
        //}
    }

    private void right(int x, int y) {
        int var = x;
        int animationDistance = 0;
        while (var != 0) {
            var -= 1;
            animationDistance += TILE_SIZE + BORDER_SIZE;
            if (tiles[var][y].stage != 0) {
                tiles[var][y].distance = animationDistance;
                tiles[var][y].speed = (tiles[var][y].distance / (TILE_SIZE + BORDER_SIZE)) * globalSpeed;
                if (tiles[x][y].stage == tiles[var][y].stage) {
                    tiles[x][y].stage *= 2;
                    addScore += (Math.log(tiles[x][y].stage) / Math.log(2) - 1) * tiles[x][y].stage;
                    tiles[var][y].stage = 0;
                    tiles[x][y].created = true;
                    move = true;
                } else if (tiles[x][y].stage == 0) {
                    tiles[x][y].stage = tiles[var][y].stage;
                    tiles[var][y].stage = 0;
                    right(x, y);
                    move = true;
                } else if (var != x - 1) {
                    tiles[x - 1][y].stage = tiles[var][y].stage;
                    tiles[var][y].stage = 0;
                    tiles[var][y].distance -= TILE_SIZE + BORDER_SIZE;
                    move = true;
                } else {
                    tiles[var][y].distance = 0;
                }
                break;
            }
        }
    }

    private void left(int x, int y) {
        int var = x;
        int animationDistance = 0;
        while (var != 3) {
            animationDistance += TILE_SIZE + BORDER_SIZE;
            var += 1;
            if (tiles[var][y].stage != 0) {
                tiles[var][y].distance = animationDistance;
                tiles[var][y].speed = (tiles[var][y].distance / (TILE_SIZE + BORDER_SIZE)) * globalSpeed;
                if (tiles[x][y].stage == tiles[var][y].stage) {
                    tiles[x][y].stage *= 2;
                    addScore += (Math.log(tiles[x][y].stage) / Math.log(2) - 1) * tiles[x][y].stage;
                    tiles[var][y].stage = 0;
                    tiles[x][y].created = true;
                    move = true;
                } else if (tiles[x][y].stage == 0) {
                    tiles[x][y].stage = tiles[var][y].stage;
                    tiles[var][y].stage = 0;
                    left(x, y);
                    move = true;
                } else if (var != x + 1) {
                    tiles[x + 1][y].stage = tiles[var][y].stage;
                    tiles[var][y].stage = 0;
                    tiles[var][y].distance -= TILE_SIZE + BORDER_SIZE;
                    move = true;
                } else {
                    tiles[var][y].distance = 0;
                }
                break;
            }
        }
    }

    private void up(int x, int y) {
        int var = y;
        int animationDistance = 0;
        while (var != 3) {
            animationDistance += TILE_SIZE + BORDER_SIZE;
            var += 1;
            if (tiles[x][var].stage != 0) {
                tiles[x][var].distance = animationDistance;
                tiles[x][var].speed = (tiles[x][var].distance / (TILE_SIZE + BORDER_SIZE)) * globalSpeed;
                if (tiles[x][y].stage == tiles[x][var].stage) {
                    tiles[x][y].stage *= 2;
                    addScore += (Math.log(tiles[x][y].stage) / Math.log(2) - 1) * tiles[x][y].stage;
                    tiles[x][var].stage = 0;
                    tiles[x][y].created = true;
                    move = true;
                } else if (tiles[x][y].stage == 0) {
                    tiles[x][y].stage = tiles[x][var].stage;
                    tiles[x][var].stage = 0;
                    up(x, y);
                    move = true;
                } else if (var != y + 1) {
                    tiles[x][y + 1].stage = tiles[x][var].stage;
                    tiles[x][var].stage = 0;
                    tiles[x][var].distance -= TILE_SIZE + BORDER_SIZE;
                    move = true;
                } else {
                    tiles[x][var].distance = 0;
                }
                //move = !(var != y + 1 && tiles[x][y + 1].stage == tiles[x][y].stage);
                /*if (var != y + 1 && tiles[x][y + 1].stage == tiles[x][y].stage) move = false;*/
                break;
            }
        }
    }

    private void down(int x, int y) {
        int var = y;
        int animationDistance = 0;
        while (var != 0) {
            animationDistance += TILE_SIZE + BORDER_SIZE;
            var -= 1;
            if (tiles[x][var].stage != 0) {
                tiles[x][var].distance = animationDistance;
                tiles[x][var].speed = (tiles[x][var].distance / (TILE_SIZE + BORDER_SIZE)) * globalSpeed;
                if (tiles[x][y].stage == tiles[x][var].stage) {
                    tiles[x][y].stage *= 2;
                    addScore += (Math.log(tiles[x][y].stage) / Math.log(2) - 1) * tiles[x][y].stage;
                    tiles[x][var].stage = 0;
                    tiles[x][y].created = true;
                    move = true;
                } else if (tiles[x][y].stage == 0) {
                    tiles[x][y].stage = tiles[x][var].stage;
                    tiles[x][var].stage = 0;
                    down(x, y);
                    move = true;
                } else if (var != y - 1) {
                    tiles[x][y - 1].stage = tiles[x][var].stage;
                    tiles[x][var].stage = 0;
                    tiles[x][var].distance -= TILE_SIZE + BORDER_SIZE;
                    move = true;
                } else {
                    tiles[x][var].distance = 0;
                }
                break;
            }
        }
    }

    private void changeStage() {
        //TODO: might not need to detach reattach, test if you can change sprite and have it update
        spawnNew();
        for (int x = 0; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                //Sprite h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house3, mEngine.getVertexBufferObjectManager()) {};
                Sprite h;
                //scene.detachChild(tiles[x][y].house);
                tiles[x][y].house.setX(x * (BORDER_SIZE + TILE_SIZE) + BORDER_SIZE + BUFFER_WIDTH);
                tiles[x][y].house.setY(y * (BORDER_SIZE + TILE_SIZE) + BORDER_SIZE + BUFFER_HEIGHT);
                switch (tiles[x][y].stage) {
                    case 0:
                        //tiles[x][y].house = null;
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house.setVisible(false);
                        break;
                    case 2:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house1, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house1);
                        break;
                    case 4:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house2, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house2);
                        break;
                    case 8:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house3, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house3);
                        break;
                    case 16:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house4, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house4);
                        break;
                    case 32:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house5, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house5);
                        break;
                    case 64:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house6, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house5);
                        break;
                    case 128:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house7, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house5);
                        break;
                    case 256:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house8, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house5);
                        break;
                    case 512:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house9, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house5);
                        break;
                    case 1024:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house10, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house5);
                        break;
                    case 2048:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house11, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house5);
                        break;
                    case 4096:
                        h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house12, mEngine.getVertexBufferObjectManager()) {
                        };
                        scene.detachChild(tiles[x][y].house);
                        tiles[x][y].house = h;
                        //h.setTextureRegion(house5);
                        break;
                }
                //tiles[x][y].house = h;
                scene.attachChild(tiles[x][y].house);
                if (tiles[x][y].created) {
                    tiles[x][y].house.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.1f, 1, 1.25f), new ScaleModifier(0.1f, 1.25f, 1)));
                    tiles[x][y].created = false;
                    //tiles[x][y].house.setScale();
                    /*Sprite h = new Sprite(tiles[x][y].house.getX(), tiles[x][y].house.getY(), house5, mEngine.getVertexBufferObjectManager()) {
                    };
                    scene.detachChild(tiles[x][y].house);
                    tiles[x][y].house = h;
                    scene.attachChild(tiles[x][y].house);*/
                } else if (tiles[x][y].nw) {
                    tiles[x][y].house.registerEntityModifier(new SequenceEntityModifier(new ScaleModifier(0.1f, 0.9f, 1f)));
                    tiles[x][y].nw = false;
                }
            }
        }
        outerloop:
        for (int x = 0; x <= 3; x++) {
            for (int y = 0; y <= 3; y++) {
                if (tiles[x][y].stage == 0) {
                    break outerloop;
                } else if (x == 3 && y == 3) {
                    for (int x2 = 0; x2 <= 3; x2++) {
                        for (int y2 = 0; y2 <= 3; y2++) {
                            if ((x2 > 0) && (tiles[x2][y2].stage == tiles[x2 - 1][y2].stage)) {
                                break outerloop;
                            } else if ((y2 > 0) && (tiles[x2][y2].stage == tiles[x2][y2 - 1].stage)) {
                                break outerloop;
                            } else if ((x2 < 3) && (tiles[x2][y2].stage == tiles[x2 + 1][y2].stage)) {
                                break outerloop;
                            } else if ((y2 < 3) && (tiles[x2][y2].stage == tiles[x2][y2 + 1].stage)) {
                                break outerloop;
                            } else if (y2 == 3 && x2 == 3) {
                                gameOver();
                            }

                        }
                    }
                }
            }
        }
        if (addScore != 0) {
            int length = String.valueOf(totalScore).length();
            totalScore += addScore;
            if (totalScore > Integer.parseInt(bestScoreSaved)) {
                bestText.setText(String.valueOf(totalScore));
                int l = bestScoreSaved.length();
                bestScoreSaved = String.valueOf(totalScore);
                if (bestScoreSaved.length() > 3 && bestScoreSaved.length() > l) {
                    bstRectSizeX += 8;
                    bstRect.setSize(bstRectSizeX, bstRect.getHeightScaled());
                    bstRect.setX(CAMERA_WIDTH - BUFFER_WIDTH - bstRectSizeX);
                    bst.setX(CAMERA_WIDTH - BUFFER_WIDTH - bstRectSizeX / 2 - bst.getWidthScaled() / 2);
                    totlRect.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - totlRectSizeX - bstRectSizeX);
                    totl.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - bstRectSizeX - totlRectSizeX / 2 - totl.getWidthScaled() / 2);
                }
                bestText.setX(CAMERA_WIDTH - BUFFER_WIDTH - bstRectSizeX / 2 - bestText.getWidth() / 2 - 2);
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                prefs.edit().putInt("Score", totalScore).apply();
            }
            totalText.setText(String.valueOf(totalScore));
            if (String.valueOf(totalScore).length() > 3 && String.valueOf(totalScore).length() > length) {
                totlRectSizeX += 8;
                totlRect.setSize(totlRectSizeX, totlRect.getHeightScaled());
                totlRect.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - totlRectSizeX - bstRectSizeX);
                totl.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - bstRectSizeX - totlRectSizeX / 2 - totl.getWidthScaled() / 2);
            }
            //totalText.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE - bstRect.getWidthScaled() - totlRect.getWidthScaled() / 2 - totalText.getWidth() / 2);
            totalText.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - bstRectSizeX - totlRectSizeX / 2 - totalText.getWidth() / 2);
            if (totlRect.getX() != CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - totlRectSizeX - bstRectSizeX || totl.getX() != CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - bstRectSizeX - totlRectSizeX / 2 - totl.getWidthScaled() / 2) {
                totlRect.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - totlRectSizeX - bstRectSizeX);
                //totl.setX(CAMERA_WIDTH - BUFFER_WIDTH - BORDER_SIZE / 2 - bstRectSizeX - totlRectSizeX / 2 - totl.getWidthScaled() / 2);
            }
            final Text t = new Text(totalText.getX(), totalText.getY(), fnt, "New High: 123456789", this.getVertexBufferObjectManager());
            t.setText("+" + String.valueOf(addScore));
            t.setScaleCenter(0, 0);
            t.setColor(new Color((float) 119 / 255, (float) 110 / 255, (float) 101 / 255));
            scene.attachChild(t);
            mEngine.registerUpdateHandler(new TimerHandler(0.05f, new ITimerCallback() {
                float alpha = 1f;
                float y = t.getY();

                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    alpha -= 0.1f;
                    y -= 5;
                    t.setAlpha(alpha);
                    t.setY(y);
                    if (alpha >= 0.1f) {
                        pTimerHandler.reset();
                    } else {
                        scene.detachChild(t);
                    }
                }

            }
            ));
            addScore = 0;
        }
    }

    private void gameOver() {
        //SharedPreferences.Editor editor = screenSize.edit();
        //editor.putInt("Score", totalScore);
        govr = true;
        scene.sortChildren();
        scene.registerTouchArea(tryagn);
        mEngine.registerUpdateHandler(new TimerHandler(0.08f, new ITimerCallback() {
            Float alpha = -0.6f;

            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                alpha += 0.1f;
                if (alpha > 0) {
                    lossRect.setVisible(true);
                    lossRect.setAlpha(alpha);
                    if (alpha <= 0.7f) {
                        pTimerHandler.reset();
                    } else {
                        gameovr.setVisible(true);
                        tryagn.setVisible(true);
                    }
                } else {
                    pTimerHandler.reset();
                }
            }

        }
        ));
    }

    private void spawnNew() {
        int xcoord = randomGenerater.nextInt(4);
        int ycoord = randomGenerater.nextInt(4);
        while (tiles[xcoord][ycoord].stage != 0) {
            xcoord = randomGenerater.nextInt(4);
            ycoord = randomGenerater.nextInt(4);
        }
        if (randomGenerater.nextInt(101) <= 15) {
            tiles[xcoord][ycoord].stage = 4;
        } else {
            tiles[xcoord][ycoord].stage = 2;
        }
        tiles[xcoord][ycoord].nw = true;
    }

    Tile[][] tiles = new Tile[4][4];

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.SurfaceViewId;
    }

    AdView adView;

    @Override
    protected void onSetContentView() {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

        if (!AndEngine.isDeviceSupported()) {
            //this device is not supported, create a toast to tell the user
            //then kill the activity
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3500);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (InterruptedException e) {
                    }
                }
            };
            this.toastOnUIThread("This device does not support AndEngine GLES2, so this game will not work. Sorry.");
            finish();
            thread.start();

            this.setContentView(relativeLayout, relativeLayoutParams);
        } else {
            this.mRenderSurfaceView = new RenderSurfaceView(this);
            mRenderSurfaceView.setRenderer(mEngine, this);

            relativeLayout.addView(mRenderSurfaceView, GameActivity.createSurfaceViewLayoutParams());

            try {
                adView = new AdView(this);
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId("ca-app-pub-6184270616715379/2782833843");
                adView.setTag("adView");
                adView.refreshDrawableState();
                adView.setVisibility(AdView.GONE);

                // Initiate a generic request to load it with an ad
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adView.loadAd(adRequest);
                    }
                });

                adView.setAdListener(new AdListener() {
                    public void onAdLoaded() {
                        adView.setVisibility(AdView.VISIBLE);
                    }
                });

                RelativeLayout.LayoutParams adViewParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                //the next line is the key to putting it on the bottom
                adViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                relativeLayout.addView(adView, adViewParams);
            } catch (Exception e) {
                //ads aren't working. oh well
            }
            this.setContentView(relativeLayout, relativeLayoutParams);
        }
    }
}