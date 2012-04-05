package za.ac.wits.elen4010.fluidsim.mpiNodalCode;
//All messaging Constants go here - makes it easier to maintain code and update constants
public class MessagingTags {
	
	static final int HostAndRank_ToServer = 50 ;
	static final int Neighbours_FromServer = 51;
	static final int Initialcondition_FromServer= 52;
	static final int BoundryInfo_ToNeighbourAbove = 61 ;
	static final int BoundryInfo_FromNeighbourBelow = 61 ;
	static final int BoundryINfo_ToNeighbourBelow = 62 ;
	static final int BoundryInfo_FromNeighbourAbove = 62 ;
	
	static final int DefaultHostandRankMsgLength = 50;

}
