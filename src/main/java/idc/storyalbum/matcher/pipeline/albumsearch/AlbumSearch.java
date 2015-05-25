package idc.storyalbum.matcher.pipeline.albumsearch;

import idc.storyalbum.model.album.Album;
import idc.storyalbum.model.album.AlbumPage;
import idc.storyalbum.model.graph.Constraint;
import idc.storyalbum.model.graph.StoryDependency;
import idc.storyalbum.model.graph.StoryEvent;
import idc.storyalbum.model.image.AnnotatedImage;
import idc.storyalbum.matcher.pipeline.DependencyUtils;
import idc.storyalbum.matcher.pipeline.PipelineContext;
import idc.storyalbum.matcher.pipeline.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * Created by yonatan on 27/4/2015.
 */
@Slf4j
public abstract class AlbumSearch {
    @Autowired
    ScoreService scoreService;

    @Value("${story-album.score.skip-dep-calc:false}")
    boolean skipDependenciesCalculation;

    double evaluateDependencies(List<StoryDependency> dependencies, AnnotatedImage i1, AnnotatedImage i2) {

        double sum = 0;
        for (StoryDependency dependency : dependencies) {
            sum += (DependencyUtils.isMatch(dependency, i1, i2) ? 1 : -1) * 0.25;
        }
        return sum;
    }

    double evaluateFitness(PipelineContext ctx, Collection<AlbumPage> assignment) {
        //calculate image score for each image
        double imagesScore = assignment.stream()
                .mapToDouble((page) -> scoreService.getImageFitScore(page.getImage(), page.getStoryEvent(), 1.0))
                .sum();
        imagesScore = imagesScore / (double) assignment.size();


        // map pairs of events to their dependencies list
        Map<ImmutablePair<Integer, Integer>, List<StoryDependency>> pairDependencies =
                ctx.getStoryGraph().getDependencies()
                        .stream()
                        .collect(groupingBy(
                                dependency ->
                                        new ImmutablePair<>(dependency.getFromEventId(), dependency.getToEventId())));

        // map each event to its annotated image
        Map<Integer, AnnotatedImage> eventToImage = assignment.stream()
                .collect(toMap((page) -> page.getStoryEvent().getId(), AlbumPage::getImage));

        double dependenciesScore = 0;
        if (!skipDependenciesCalculation) {
            // calculate each dependencies' score
            double sum = 0;
            int count = 0;
            for (ImmutablePair<Integer, Integer> pair : pairDependencies.keySet()) {
                AnnotatedImage i1 = eventToImage.get(pair.getLeft());
                AnnotatedImage i2 = eventToImage.get(pair.getRight());
                List<StoryDependency> dependencies = pairDependencies.get(pair);
                count += dependencies.size();
                sum += evaluateDependencies(dependencies, i1, i2);
            }

            if (count > 0) {
                dependenciesScore = sum / (double) count;
            }
        }
        return imagesScore + dependenciesScore;
    }

    List<AlbumPage> sortPages(Collection<AlbumPage> pages) {
        List<AlbumPage> result = new ArrayList<>(pages);
        result.sort((o1, o2) -> o1.getStoryEvent().getId() - o2.getStoryEvent().getId());
        return result;
    }

    void debugShowAlbum(Album album, PipelineContext ctx) {
        if (log.isDebugEnabled()) {
            int i = 0;
            for (AlbumPage albumPage : album.getPages()) {
                i++;
                log.debug("Page {}", i);
                AnnotatedImage image = albumPage.getImage();
                log.debug("  Image: {}", image.getImageFilename());
                log.debug("    Characters: {}", image.getCharacterIds());
                log.debug("    Location: {}", image.getLocationId());
                log.debug("    Items: {}", image.getItemIds());
                StoryEvent storyEvent = albumPage.getStoryEvent();
                log.debug("  Event: {} {}", storyEvent.getId(), storyEvent.getName());
                for (Constraint constraint : storyEvent.getConstraints()) {
                    log.debug("    Constraint: {}", constraint);
                }
                Set<StoryDependency> dependencies = ctx.getInDependenciesForEvent(storyEvent);
                for (StoryDependency dependency : dependencies) {
                    log.debug("    Dependencies: {}", dependency);

                }
            }
        }
    }

    public SortedSet<Album> findAlbums(PipelineContext ctx) {
        SortedSet<Album> bestAlbums = findAlbumsImpl(ctx);
        debugShowAlbum(bestAlbums.first(), ctx);
        return bestAlbums;
    }

    public abstract SortedSet<Album> findAlbumsImpl(PipelineContext ctx);
}
