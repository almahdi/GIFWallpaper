package software.eligo.com.gifwallpaper;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.service.wallpaper.WallpaperService;

import java.io.IOException;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Bermud06 on 15.11.2015.
 */
public class GIFWallpaperService extends WallpaperService {
    @Override
    public WallpaperService.Engine onCreateEngine() {

        try {
            Movie movie=Movie.decodeStream(getResources().getAssets().open("tugl.gif"));
            return new GIFWallpaperEngine(movie);
        } catch (IOException e) {
            Log.d("GIF","Unable to load asset");
            return null;
        }

    }
    public class GIFWallpaperEngine extends WallpaperService.Engine {
        private final int frameDuration=20; //20 millis, i.e. 1000/20 =50 frames in a second

        private SurfaceHolder holder; //??? engine uses it
        private Movie movie; // to hold animated gif
        private boolean visible; //let android know if the live wallpaper visible on the screen
        private android.os.Handler handler; //this object is responsible for drawing gif

        public GIFWallpaperEngine(Movie movie)
        {
            this.movie=movie;
            handler=new android.os.Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder=surfaceHolder;
        }

        private Runnable drafGif=new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        private void draw(){
            if (visible)
            {
                Canvas canvas=holder.lockCanvas();
                canvas.save();
                canvas.scale(2f, 2f);
                movie.draw(canvas, -100, 0);

                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));
                handler.removeCallbacks(drafGif);
                handler.postDelayed(drafGif,frameDuration);

            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible=visible;
            if (visible)
            {
                handler.post(drafGif);
            }
            else
            {
                handler.removeCallbacks(drafGif);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drafGif);
        }
    }
}
