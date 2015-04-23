package idc.storyalbum.matcher.pipeline;

import idc.storyalbum.matcher.model.album.Album;

import java.util.Set;

/**
 * Created by yonatan on 19/4/2015.
 */
public interface AlbumSearch {
    Set<Album> findAlbums(PipelineContext ctx);
}
