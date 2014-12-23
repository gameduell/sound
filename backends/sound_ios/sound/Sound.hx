/**
 * @author kgar
 * @date  23/12/14 
 * Copyright (c) 2014 GameDuell GmbH
 */
package sound;
import msignal.Signal;
import types.Data;
///=================///
/// Sound IOS       ///
///                 ///
///=================///
class Sound
{
    public var volume(set_volume,default): Float;
    public var loop(set_loop,default): Bool;
    public var length(null,get_length): Float;
    public var position(null,get_position): Float;


    public var onPlaybackComplete(default,null): Signal1;

    public function new(data: Data)
    {
        //TODO: Impliment me
    }

    public function play(): Void
    {
        //TODO: Impliment me
    }

    public function stop(): Void
    {
        //TODO: Impliment me
    }

    public function pause(): Void
    {
        //TODO: Impliment me
    }

    public function mute(): Void
    {
        //TODO: Impliment me
    }

    /// here you can do platform specific logic to set the sound volume
    public function set_volume(value: Float): Float
    {
        volume = value;
        return volume;
    }

    /// here you can do platform specific logic to make the sound loop
    public function set_loop(value: Bool): Bool
    {
        loop = value;
        return loop;
    }

    /// get the length of the current sound
    public function get_length(): Float
    {
        //TODO: Impliment me
        return 0.0;
    }

    /// get the current time of the current sound
    public function get_position(): Float
    {
        //TODO: Impliment me
        return 0.0;
    }
}