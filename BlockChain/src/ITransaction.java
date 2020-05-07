public interface ITransaction {
    public boolean containsWitnesses();  // returns the witness flag
    public void setIPCount(int count);
    public void setIP(int[] ip);        //len of ip = IPcounter
    public void setWitnesses(int[] ws); // len of ws = IPcounter , sets witness flag to true
    public int[] getOP();
}


//- Flag: indicates if the transaction hash will contain the witnesses or not.
//- Ip-counter: integer specifying the number of inputs.
//- list of inputs: length of this list is Ip-Counter, an input specifies the previous
//transaction, the index of the output used as this input, and the signature of payer
//(the previous transaction hash ca be included in the witnesses).
//- Op-counter: integer specifying the number of outputs.
//- list of outputs: length of this list is Op-Counter, an output specifies the value,
//the index of the output, and the public key of the payee.
//- list of witnesses: 1 for each input.
