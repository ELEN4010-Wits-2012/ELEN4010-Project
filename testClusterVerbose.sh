
#!/bin/bash

#Remove old test files
rm -Rf testResults
rm -Rf logResults

mkdir -p testResults
mkdir -p logResults

for i in {2..32..2}
do

    #EXECUTE THE MPI PROGRAM
    echo "Starting execution of $i threads"
    mpiexec -n $i java za.ac.wits.elen4010.fluidsim.mpiNodalCode.Main >> logResults/$i"nodesLog.txt"
    
    #NOW GATHER ALL DATA
    mkdir testResults/$i
    #Gather once off data
    cp *initiliseSlaveNodes.txt testResults/$i/initiliseSlaveNodes.txt
    cp *run.txt testResults/$i/run.txt
    #Gather all sendBoundary processes
    cat *sendBoundary* >> testResults/$i/sendBoundaryProcesses.csv
    #Gather all the sendRenderData processes
    cat *sendRenderData.txt >> testResults/$i/sendRenderData.csv
    #Gather all the steps, Bottom, Top, Even and Odd
    cat *step* >>testResults/$i/steppingOfAllProcesses.csv
    
    #WE HAVE WHAT WE NEED, NOW REMOVE ALL OUTPUTS AND GIVE NEXT SIM A CLEAN RUN
    rm *.txt
    rm *.out
  
    # If you start too quickly slave nodes on the other machines may still send you messages and that's a recipe for crap
    # Give the network and all machines 10 seconds to return to a "calm" state.
    echo "Sleeping for 10 seconds"
    sleep 10
    
done





