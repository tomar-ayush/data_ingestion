"use client"

import { useState, useRef } from "react"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { useToast } from "@/hooks/use-toast"

export default function FlatfileForm({ onPreview }) {
  const [filePath, setFilePath] = useState("")
  const [loading, setLoading] = useState(false)
  const fileInputRef = useRef(null)
  const { toast } = useToast()

  const handleFileChange = (e) => {
    const file = e.target.files[0]
    if (!file) return

    // In a real app, you would handle file upload to server
    // For demo purposes, we'll just use the file name
    setFilePath(file.name)
  }

  const handlePreview = async () => {
    if (!filePath) {
      toast({
        title: "No File Selected",
        description: "Please select a file first",
        variant: "destructive",
      })
      return
    }

    setLoading(true)

    try {
      await onPreview({ filePath })
    } catch (error) {
      console.error("Preview error:", error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-4 border p-4 rounded-md">
      <div className="space-y-2">
        <Label htmlFor="file">File Path</Label>
        <div className="flex gap-2">
          <input
            type="text"
            value={filePath}
            readOnly
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
            placeholder="Select a file..."
          />
          <Button type="button" variant="outline" onClick={() => fileInputRef.current?.click()}>
            Browse
          </Button>
          <input
            ref={fileInputRef}
            id="file"
            type="file"
            accept=".csv,.tsv,.txt"
            onChange={handleFileChange}
            className="hidden"
          />
        </div>
        <p className="text-xs text-muted-foreground">Supported formats: CSV, TSV, TXT</p>
      </div>

      <Button type="button" onClick={handlePreview} disabled={!filePath || loading}>
        {loading ? "Loading..." : "Preview Data"}
      </Button>
    </div>
  )
}
