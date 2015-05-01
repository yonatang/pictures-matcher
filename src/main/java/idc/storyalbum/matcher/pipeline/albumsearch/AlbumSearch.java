package idc.storyalbum.matcher.pipeline.albumsearch;

import idc.storyalbum.matcher.model.album.Album;
import idc.storyalbum.matcher.model.album.AlbumPage;
import idc.storyalbum.matcher.model.graph.StoryDependency;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import idc.storyalbum.matcher.pipeline.DependencyUtils;
import idc.storyalbum.matcher.pipeline.PipelineContext;
import idc.storyalbum.matcher.pipeline.ScoreService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * Created by yonatan on 27/4/2015.
 */
public abstract class AlbumSearch {
    @Autowired
    ScoreService scoreService;

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
                .mapToDouble((page) -> scoreService.getImageFitScore(page.getImage(), page.getStoryEvent()))
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
        double dependenciesScore = 0;
        if (count > 0) {
            dependenciesScore = sum / (double) count;
        }
        return imagesScore + dependenciesScore;
    }

    List<AlbumPage> sortPages(Collection<AlbumPage> pages) {
        List<AlbumPage> result = new ArrayList<>(pages);
        result.sort((o1, o2) -> o1.getStoryEvent().getId() - o2.getStoryEvent().getId());
        return result;
    }

    public abstract SortedSet<Album> findAlbums(PipelineContext ctx);
}
