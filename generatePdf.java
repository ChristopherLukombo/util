public byte[] generatePDFsIn(List<Invoice> invoices) throws IOException, InterruptedException {
       ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
       try (ZipOutputStream zipOutputStream = new ZipOutputStream(zipStream)) {
           for (int i = 0; i < invoices.size(); i++) {
        Invoice invoice = invoices.get(i);
        @SuppressWarnings("unused")
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
      
        final int index = i;
        executorService.execute(() -> {
            try {
         byte[] pdfContent = generatePDF(invoice);
         synchronized (zipOutputStream) {
             zipOutputStream.putNextEntry(new ZipEntry("invoice_" + (index + 1) + ".pdf"));
             zipOutputStream.write(pdfContent);
             zipOutputStream.closeEntry();
         }
            } catch (IOException e) {
         e.printStackTrace();
            }
        });
           }
           executorService.shutdown();
           executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
       }
       return zipStream.toByteArray();
    }