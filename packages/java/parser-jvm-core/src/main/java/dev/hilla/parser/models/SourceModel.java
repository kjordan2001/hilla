package dev.hilla.parser.models;

@Deprecated
public interface SourceModel extends Model {
    @Override
    default boolean isSource() {
        return true;
    }
}
