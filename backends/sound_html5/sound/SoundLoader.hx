/**
 * @author kgar
 * @date  07/05/15 
 * Copyright (c) 2014 GameDuell GmbH
 */
package sound;
import msignal.Signal.Signal1;
class SoundLoader
{
    public var soundLoaded: Signal1<String>;
    private static var soundLoaderInstance: sound.SoundLoader;

    private function new()
    {
        //fallback will be in the same order
        createjs.soundjs.Sound.registerPlugins([createjs.soundjs.WebAudioPlugin, createjs.soundjs.HTMLAudioPlugin]);
        soundLoaded = new Signal1();
    }

    public static function getInstance(): SoundLoader
    {
        if(soundLoaderInstance == null)
        {
            soundLoaderInstance = new SoundLoader();
        }
        return soundLoaderInstance;
    }

    public function loadSound(fileUrl: String): Void
    {
        createjs.soundjs.Sound.addEventListener("fileload", soundHandleLoad);
        createjs.soundjs.Sound.registerSound(fileUrl, fileUrl);
    }

    public function soundHandleLoad(event: Dynamic): Void
    {
        soundLoaded.dispatch(event.id);
    }
}