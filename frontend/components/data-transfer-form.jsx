"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useToast } from "@/hooks/use-toast"
import ClickHouseForm from "./clickhouse-form"
import FlatfileForm from "./flatfile-form"
import PreviewModal from "./preview-modal"
import axios from "axios"

export default function DataTransferForm() {
  const [sourceType, setSourceType] = useState("")
  const [targetType, setTargetType] = useState("")
  const [clickHouseConfig, setClickHouseConfig] = useState({
    host: "",
    port: "",
    database: "",
    user: "default",
    jwtToken: "",
    table: "",
    columns: [],
  })
  const [flatFileConfig, setFlatFileConfig] = useState({
    filePath: "",
    delimiter: ",",
    columns: [],
  })
  const [previewData, setPreviewData] = useState(null)
  const [showPreviewModal, setShowPreviewModal] = useState(false)
  const [previewSource, setPreviewSource] = useState("")
  const [isConnected, setIsConnected] = useState(false)
  const { toast } = useToast()

  const handleSourceChange = (value) => {
    if (value === targetType) {
      toast({
        title: "Invalid Selection",
        description: "Source and target cannot be the same",
        variant: "destructive",
      })
      return
    }
    setSourceType(value)
  }

  const handleTargetChange = (value) => {
    if (value === sourceType) {
      toast({
        title: "Invalid Selection",
        description: "Source and target cannot be the same",
        variant: "destructive",
      })
      return
    }
    setTargetType(value)
  }

  const handleClickHouseConnect = async (config) => {
    try {
      // In a real app, you would make an API call to connect to ClickHouse
      // For demo purposes, we'll simulate a successful connection
      console.log("Connecting to ClickHouse with config:", config)

      // Simulate API call
      await new Promise((resolve) => setTimeout(resolve, 1000))

      setClickHouseConfig({
        ...config,
        columns: [],
      })

      setIsConnected(true)
      toast({
        title: "Connection Successful",
        description: "Successfully connected to ClickHouse database",
      })

      return true
    } catch (error) {
      toast({
        title: "Connection Failed",
        description: error.message || "Failed to connect to ClickHouse database",
        variant: "destructive",
      })
      return false
    }
  }

  const handleClickHousePreview = async () => {
    try {
      // Simulate fetching preview data
      console.log("Fetching preview data from ClickHouse")
      const token = JSON.parse(localStorage.getItem('user'));
      const headers = token ? { Authorization: `Bearer ${token.token}` } : {};
      const response = await axios.get('http://localhost:8080/api/ingest/preview/clickhouse', {
        params: {
          host: clickHouseConfig.host,
          port: clickHouseConfig.port,
          database: clickHouseConfig.database,
          user: clickHouseConfig.user,
          jwtToken,
          table: clickHouseConfig.table,
        },
        headers,
      });
      console.log(response.data);

      // Simulate API call
      // await new Promise((resolve) => setTimeout(resolve, 1000))

      // Mock data for preview
      const mockData = {
        columns: response.data.columns,
        rows: response.data.data
      }

      setPreviewData(mockData)
      setPreviewSource("clickhouse")
      setShowPreviewModal(true)
    } catch (error) {
      toast({
        title: "Preview Failed",
        description: error.message || "Failed to preview ClickHouse data",
        variant: "destructive",
      })
    }
  }

  const handleFlatfilePreview = async (config) => {
    try {
      // Log the file path for debugging
      console.log("Fetching preview data from Flatfile", config)

      // Get file extension to determine delimiter
      const fileExtension = config.filePath.split(".").pop().toLowerCase()
      let delimiter = config.delimiter || ","

      if (fileExtension === "tsv") {
        delimiter = "\t"
      } else if (fileExtension === "csv") {
        delimiter = ","
      }

      setFlatFileConfig({
        ...config,
        delimiter,
      })

      // In a real application, we would send this to the server
      // For client-side parsing, we need the actual File object
      const fileInput = document.querySelector('input[type="file"]')
      
      if (!fileInput || !fileInput.files || fileInput.files.length === 0) {
        throw new Error("No file selected")
      }

      const file = fileInput.files[0]
      
      // Parse the CSV/TSV file using PapaParse
      Papa.parse(file, {
        delimiter: delimiter,
        header: true,
        preview: 10, // Parse only first 10 rows for preview
        complete: (results) => {
          if (results.errors && results.errors.length > 0) {
            throw new Error(`Parsing error: ${results.errors[0].message}`)
          }

          // Transform the parsed data into the expected format
          const columns = results.meta.fields || []
          const rows = results.data || []

          const previewData = {
            columns,
            rows: rows.slice(0, 3) // Take only first 3 rows for preview
          }

          setPreviewData(previewData)
          setPreviewSource("flatfile")
          setShowPreviewModal(true)
        },
        error: (error) => {
          throw new Error(`Failed to parse file: ${error.message}`)
        }
      })
    } catch (error) {
      toast({
        title: "Preview Failed",
        description: error.message || "Failed to preview Flatfile data",
        variant: "destructive",
      })
    }
  }

  const handleColumnSelect = (selectedColumns) => {
    if (previewSource === "clickhouse") {
      setClickHouseConfig((prev) => ({
        ...prev,
        columns: selectedColumns,
      }))
    } else {
      setFlatFileConfig((prev) => ({
        ...prev,
        columns: selectedColumns,
      }))
    }

    setShowPreviewModal(false)
  }

  const handleIngest = async () => {
    try {
      // Validate configurations
      if (
        sourceType === "CLICKHOUSE" &&
        (!clickHouseConfig.host || !clickHouseConfig.table || clickHouseConfig.columns.length === 0)
      ) {
        throw new Error("ClickHouse configuration is incomplete")
      }

      if (sourceType === "FLATFILE" && (!flatFileConfig.filePath || flatFileConfig.columns.length === 0)) {
        throw new Error("Flatfile configuration is incomplete")
      }

      // Prepare payload
      const payload = {
        sourceType,
        targetType,
        clickHouseConfig:
          sourceType === "CLICKHOUSE" ? clickHouseConfig : targetType === "CLICKHOUSE" ? clickHouseConfig : null,
        flatFileConfig: sourceType === "FLATFILE" ? flatFileConfig : targetType === "FLATFILE" ? flatFileConfig : null,
      }

      console.log("Ingesting data with payload:", payload)

      // Simulate API call
      await new Promise((resolve) => setTimeout(resolve, 1500))

      toast({
        title: "Ingestion Successful",
        description: `Data successfully transferred from ${sourceType} to ${targetType}`,
      })
    } catch (error) {
      toast({
        title: "Ingestion Failed",
        description: error.message || "Failed to ingest data",
        variant: "destructive",
      })
    }
  }

  return (
    <div className="space-y-8">
      <Card>
        <CardHeader>
          <CardTitle>Data Transfer Configuration</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-4">
              <h3 className="text-lg font-medium">Source</h3>
              <Select value={sourceType} onValueChange={handleSourceChange}>
                <SelectTrigger>
                  <SelectValue placeholder="Select source type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="CLICKHOUSE">ClickHouse</SelectItem>
                  <SelectItem value="FLATFILE">Flatfile</SelectItem>
                </SelectContent>
              </Select>

              {sourceType === "CLICKHOUSE" && (
                <ClickHouseForm
                  onConnect={handleClickHouseConnect}
                  onPreview={handleClickHousePreview}
                  isConnected={isConnected}
                />
              )}

              {sourceType === "FLATFILE" && <FlatfileForm onPreview={handleFlatfilePreview} />}
            </div>

            <div className="space-y-4">
              <h3 className="text-lg font-medium">Target</h3>
              <Select value={targetType} onValueChange={handleTargetChange}>
                <SelectTrigger>
                  <SelectValue placeholder="Select target type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="CLICKHOUSE">ClickHouse</SelectItem>
                  <SelectItem value="FLATFILE">Flatfile</SelectItem>
                </SelectContent>
              </Select>

              {targetType === "CLICKHOUSE" && (
                <ClickHouseForm
                  onConnect={handleClickHouseConnect}
                  onPreview={handleClickHousePreview}
                  isConnected={isConnected}
                />
              )}

              {targetType === "FLATFILE" && <FlatfileForm onPreview={handleFlatfilePreview} />}
            </div>
          </div>

          {sourceType && targetType && (
            <div className="mt-8 flex justify-center">
              <button
                onClick={handleIngest}
                className="px-6 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors"
                disabled={
                  (sourceType === "CLICKHOUSE" && (!isConnected || clickHouseConfig.columns.length === 0)) ||
                  (sourceType === "FLATFILE" && flatFileConfig.columns.length === 0)
                }
              >
                Transfer Data
              </button>
            </div>
          )}
        </CardContent>
      </Card>

      {showPreviewModal && previewData && (
        <PreviewModal
          data={previewData}
          onClose={() => setShowPreviewModal(false)}
          onSelectColumns={handleColumnSelect}
        />
      )}
    </div>
  )
}