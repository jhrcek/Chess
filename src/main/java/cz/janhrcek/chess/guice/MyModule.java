package cz.janhrcek.chess.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import cz.janhrcek.chess.FEN.FenParser;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.model.api.RuleChecker;
import cz.janhrcek.chess.model.impl.FIDERuleChecker;
import cz.janhrcek.chess.model.impl.Game;
import cz.janhrcek.chess.model.impl.GameBrowserImpl;
import cz.janhrcek.chess.model.impl.GameStateFactoryImpl;

/**
 * Google -guice module that binds interfaces to their implementation classes.
 *
 * @author jhrcek
 */
public class MyModule extends AbstractModule {

    @Override
    protected void configure() {
        //bind(GameBrowser.class).to(GameBrowserImpl.class);
        bind(GameStateFactory.class).to(GameStateFactoryImpl.class);
        bind(RuleChecker.class).to(FIDERuleChecker.class);
        bind(Game.class); //TODO fix this - write an interface for Game perhaps..
        bind(String.class).annotatedWith(FenString.class).toInstance(FenParser.INITIAL_STATE_FEN);
    }

    @Provides
    @Singleton
    GameBrowser provideGameBrowser(Game game) {
        return new GameBrowserImpl(game);
    }
}
