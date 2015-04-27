package idc.storyalbum.matcher.pipeline.albumsearch;

/**
 * Created by yonatan on 27/4/2015.
 */
public interface AlbumSearchFactory {
    AlbumSearch getAlbumSearch(String selector);
}
