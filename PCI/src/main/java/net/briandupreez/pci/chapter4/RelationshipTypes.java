package net.briandupreez.pci.chapter4;

import org.neo4j.graphdb.RelationshipType;

public enum RelationshipTypes implements RelationshipType
{
    LINK_TO,
    CONTAINS
}