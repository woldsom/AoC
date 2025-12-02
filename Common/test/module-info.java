module com.w_wins.Common.Test {
    requires com.w_wins.Common;
    requires org.junit.jupiter;
    exports com.w_wins.pathfindingtest;
    opens com.w_wins.pathfindingtest to org.junit.platform.commons;
}