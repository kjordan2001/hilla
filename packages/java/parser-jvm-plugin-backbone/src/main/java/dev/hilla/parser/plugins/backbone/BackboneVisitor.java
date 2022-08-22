package dev.hilla.parser.plugins.backbone;

import java.util.function.Supplier;

import dev.hilla.parser.core.NodePath;
import dev.hilla.parser.core.Visitor;
import dev.hilla.parser.models.ClassInfoModel;

final class BackboneVisitor implements Visitor {
    private final Supplier<Integer> baseOrder;
    private final EndpointProcessor endpointProcessor;
    private final EntityProcessor entityProcessor;
    private final PathRecognizer recognizer;
    private final int shift;

    BackboneVisitor(Context context, Supplier<Integer> baseOrder, int shift) {
        this.baseOrder = baseOrder;
        this.endpointProcessor = new EndpointProcessor(context);
        this.entityProcessor = new EntityProcessor(context);
        this.recognizer = new PathRecognizer(
                context.getEndpointAnnotationName());
        this.shift = shift;
    }

    @Override
    public void enter(NodePath path) {
        if (path.isRemoved()) {
            return;
        }

        var model = path.getModel();

        if (model instanceof ClassInfoModel) {
            var cls = (ClassInfoModel) model;

            if (path instanceof NodePath.ClassDeclaration) {
                // We are exploring a class declaration; the only parent is a
                // PackageInfoModel.
                if (recognizer.isEndpointClass(cls)) {
                    endpointProcessor.process(cls);
                } else if (recognizer.isEntityClass(cls)) {
                    entityProcessor.process(cls);
                }
            } else {
                // We entered a class mention, either by class reference or as
                // an inner class declaration; it means that we know when and
                // by which node this class is mentioned.
                recognizer.addClassMention(path);
            }
        }
    }

    @Override
    public int getOrder() {
        return baseOrder.get() + shift;
    }
}